package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.client.OrdenClient;
import com.farmacyfood.kitchen.dto.ItemPlanDTO;
import com.farmacyfood.kitchen.dto.PlanDiarioResponseDTO;
import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import com.farmacyfood.kitchen.entity.postgres.DailyPlan;
import com.farmacyfood.kitchen.entity.postgres.PlanItem;
import com.farmacyfood.kitchen.exception.PlanAlreadyExistsException;
import com.farmacyfood.kitchen.exception.PlanNotFoundException;
import com.farmacyfood.kitchen.repository.PlanDiarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlanDiarioServiceImpl implements PlanDiarioService {

    private final PlanDiarioRepository planDiarioRepository;
    private final OrdenClient ordenClient;

    private static final int DIAS_HISTORIAL = 7;

    @Override
    @Transactional(readOnly = true)
    public PlanDiarioResponseDTO getPlanByDate(LocalDate date) {
        DailyPlan plan = planDiarioRepository.findByDate(date)
            .orElseThrow(() -> new PlanNotFoundException("No existe plan para la fecha: " + date));
        return toDTO(plan);
    }

    @Override
    @Transactional
    public PlanDiarioResponseDTO generarPlan(LocalDate date) {
        // Si ya existe un plan para esta fecha, no lo regenero
        if (planDiarioRepository.findByDate(date).isPresent()) {
            throw new PlanAlreadyExistsException("Ya existe un plan para la fecha: " + date);
        }

        // Traigo las ventas de los últimos 7 días desde el servicio de órdenes
        LocalDate desde = date.minusDays(DIAS_HISTORIAL);
        List<VentaHistoricaResponseDTO> ventas = ordenClient.getVentasRecientes(desde, date.minusDays(1));

        // Calculo los items del plan a partir de las ventas (promedio redondeado para arriba)
        List<PlanItem> items = calcularItemsPlan(ventas);

        // Creo el plan y le agrego los items
        DailyPlan plan = DailyPlan.builder().date(date).build();
        for (PlanItem item : items) {
            plan.addItem(item);
        }

        DailyPlan saved = planDiarioRepository.save(plan);
        return toDTO(saved);
    }

    // Agrupa las ventas por producto y calcula el promedio de cantidad vendida (redondeado para arriba).
    // Solo incluye productos con promedio > 0.
    private List<PlanItem> calcularItemsPlan(List<VentaHistoricaResponseDTO> ventas) {
        // Agrupo las ventas por productId
        Map<Long, List<VentaHistoricaResponseDTO>> ventasPorProducto = new HashMap<>();
        for (VentaHistoricaResponseDTO venta : ventas) {
            ventasPorProducto
                .computeIfAbsent(venta.productId(), k -> new ArrayList<>())
                .add(venta);
        }

        // Para cada producto, calculo el promedio de cantidad y armo el PlanItem
        List<PlanItem> items = new ArrayList<>();
        for (Map.Entry<Long, List<VentaHistoricaResponseDTO>> entry : ventasPorProducto.entrySet()) {
            Long productId = entry.getKey();
            List<VentaHistoricaResponseDTO> ventasProducto = entry.getValue();

            String productName = ventasProducto.get(0).productName();

            // Calculo el promedio manualmente (suma / cantidad)
            int suma = 0;
            for (VentaHistoricaResponseDTO v : ventasProducto) {
                suma += v.quantity();
            }
            int promedio = (int) Math.ceil((double) suma / ventasProducto.size());

            if (promedio > 0) {
                PlanItem item = PlanItem.builder()
                    .productId(productId)
                    .productName(productName)
                    .suggestedQuantity(promedio)
                    .build();
                items.add(item);
            }
        }
        return items;
    }

    private PlanDiarioResponseDTO toDTO(DailyPlan plan) {
        List<ItemPlanDTO> itemDTOs = new ArrayList<>();
        for (PlanItem item : plan.getItems()) {
            itemDTOs.add(new ItemPlanDTO(item.getProductId(), item.getProductName(), item.getSuggestedQuantity()));
        }
        return new PlanDiarioResponseDTO(plan.getId(), plan.getDate(), itemDTOs, plan.getCreatedAt());
    }
}
