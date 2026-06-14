package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.client.OrdenClient;
import com.farmacyfood.kitchen.dto.ItemPlanDTO;
import com.farmacyfood.kitchen.dto.PlanDiarioResponseDTO;
import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import com.farmacyfood.kitchen.entity.postgres.DailyPlan;
import com.farmacyfood.kitchen.entity.postgres.PlanItem;
import com.farmacyfood.kitchen.exception.PlanNotFoundException;
import com.farmacyfood.kitchen.repository.PlanDiarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        LocalDate desde = date.minusDays(DIAS_HISTORIAL);

        List<VentaHistoricaResponseDTO> ventas = ordenClient.getVentasRecientes(desde, date.minusDays(1));

        Map<Long, List<VentaHistoricaResponseDTO>> ventasPorProducto = ventas.stream()
            .collect(Collectors.groupingBy(VentaHistoricaResponseDTO::productId));

        List<PlanItem> items = new ArrayList<>();
        for (Map.Entry<Long, List<VentaHistoricaResponseDTO>> entry : ventasPorProducto.entrySet()) {
            Long productId = entry.getKey();
            List<VentaHistoricaResponseDTO> ventasProducto = entry.getValue();

            String productName = ventasProducto.get(0).productName();
            int promedio = (int) Math.ceil(
                ventasProducto.stream().mapToInt(VentaHistoricaResponseDTO::quantity).average().orElse(0)
            );

            if (promedio > 0) {
                PlanItem item = PlanItem.builder()
                    .productId(productId)
                    .productName(productName)
                    .suggestedQuantity(promedio)
                    .build();
                items.add(item);
            }
        }

        DailyPlan plan = planDiarioRepository.findByDate(date).orElse(null);
        if (plan != null) {
            plan.clearItems();
        } else {
            plan = DailyPlan.builder().date(date).build();
        }

        for (PlanItem item : items) {
            plan.addItem(item);
        }

        DailyPlan saved = planDiarioRepository.save(plan);
        return toDTO(saved);
    }

    private PlanDiarioResponseDTO toDTO(DailyPlan plan) {
        List<ItemPlanDTO> itemDTOs = plan.getItems().stream()
            .map(item -> new ItemPlanDTO(item.getProductId(), item.getProductName(), item.getSuggestedQuantity()))
            .toList();
        return new PlanDiarioResponseDTO(plan.getId(), plan.getDate(), itemDTOs, plan.getCreatedAt());
    }
}
