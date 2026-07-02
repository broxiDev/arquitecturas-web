package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.client.FridgeClient;
import com.farmacyfood.kitchen.client.OrdenClient;
import com.farmacyfood.kitchen.dto.FridgeRemainderDTO;
import com.farmacyfood.kitchen.dto.ItemPlanDTO;
import com.farmacyfood.kitchen.dto.PlanDiarioResponseDTO;
import com.farmacyfood.kitchen.dto.ProductoVentaDTO;
import com.farmacyfood.kitchen.entity.postgres.DailyPlan;
import com.farmacyfood.kitchen.entity.postgres.PlanItem;
import com.farmacyfood.kitchen.exception.PlanAlreadyExistsException;
import com.farmacyfood.kitchen.exception.PlanNotFoundException;
import com.farmacyfood.kitchen.repository.PlanDiarioRepository;
import com.farmacyfood.kitchen.util.PlanCalculatorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanDiarioServiceImpl implements PlanDiarioService {

    private final PlanDiarioRepository planDiarioRepository;
    private final OrdenClient ordenClient;
    private final FridgeClient fridgeClient;

    private static final int HISTORY_DAYS = 7;

    @Override
    @Transactional(readOnly = true)
    public PlanDiarioResponseDTO getPlanByDate(LocalDate date, Long cocinaId) {
        // Busco el plan por fecha y cocina
        DailyPlan plan = planDiarioRepository.findByDateAndCocinaId(date, cocinaId)
            .orElseThrow(() -> new PlanNotFoundException("No existe plan para la fecha: " + date + " y cocina: " + cocinaId));
        return toDTO(plan);
    }

    @Override
    @Transactional
    public PlanDiarioResponseDTO generarPlan(LocalDate date, Long cocinaId) {
        try {
            // Si ya existe un plan para esta fecha y cocina, no lo regenero
            if (planDiarioRepository.findByDateAndCocinaId(date, cocinaId).isPresent()) {
                throw new PlanAlreadyExistsException("Ya existe un plan para la fecha: " + date + " y cocina: " + cocinaId);
            }

            // Traigo las ventas de los ultimos 7 dias desde el servicio de ordenes (filtrado por cocina)
            LocalDate from = date.minusDays(HISTORY_DAYS);
            LocalDate to = date.minusDays(1);
            List<ProductoVentaDTO> sales = ordenClient.getSalesByKitchen(cocinaId, from, to);

            // Traigo el remanente de heladeras asociadas a este catalogo
            List<FridgeRemainderDTO> fridges = fridgeClient.getRemainderByKitchen();

            // Calculo total vendido - stock en heladera = lo que falta producir
            Map<Long, Integer> totals = PlanCalculatorUtils.calculateTotalSales(sales);
            Map<Long, Integer> remainders = PlanCalculatorUtils.calculateTotalRemainder(fridges);
            Map<Long, Integer> suggestedQuantities = PlanCalculatorUtils.calculateSuggestedQuantities(totals, remainders);

            // Creo el plan y le agrego los items
            DailyPlan plan = DailyPlan.builder().date(date).cocinaId(cocinaId).build();
            for (Map.Entry<Long, Integer> entry : suggestedQuantities.entrySet()) {
                Long productId = entry.getKey();
                int suggested = entry.getValue();
                // Busco el nombre del producto en la lista de ventas
                String productName = getProductName(sales, productId);
                PlanItem item = PlanItem.builder()
                    .productId(productId)
                    .productName(productName)
                    .suggestedQuantity(suggested)
                    .build();
                plan.addItem(item);
            }

            DailyPlan saved = planDiarioRepository.save(plan);
            return toDTO(saved);
        } catch (Exception e) {
            throw e;
        }
    }

    // Busca el nombre del producto en la lista de ventas por su productId
    private String getProductName(List<ProductoVentaDTO> sales, Long productId) {
        for (ProductoVentaDTO sale : sales) {
            if (sale.productId().equals(productId)) {
                return sale.productName();
            }
        }
        return "Producto Desconocido";
    }

    private PlanDiarioResponseDTO toDTO(DailyPlan plan) {
        List<ItemPlanDTO> itemDTOs = new ArrayList<>();
        for (PlanItem item : plan.getItems()) {
            itemDTOs.add(new ItemPlanDTO(item.getProductId(), item.getProductName(), item.getSuggestedQuantity()));
        }
        return new PlanDiarioResponseDTO(plan.getId(), plan.getDate(), plan.getCocinaId(), itemDTOs, plan.getCreatedAt());
    }
}
