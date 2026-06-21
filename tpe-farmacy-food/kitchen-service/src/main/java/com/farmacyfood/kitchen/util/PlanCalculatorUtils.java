package com.farmacyfood.kitchen.util;

import com.farmacyfood.kitchen.dto.FridgeRemainderDTO;
import com.farmacyfood.kitchen.dto.ProductRemainderDTO;
import com.farmacyfood.kitchen.dto.ProductoVentaDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Clase utilitaria para los cálculos del plan diario de producción
// Mantiene la lógica de cálculo separada del servicio para mantenerlo simple
public class PlanCalculatorUtils {

    private PlanCalculatorUtils() {
        // Clase utilitaria, no se instancia
    }

    // Calcula el total vendido por producto en el período
    // Retorna un Map: productId → cantidad total vendida
    public static Map<Long, Integer> calculateTotalSales(List<ProductoVentaDTO> sales) {
        Map<Long, Integer> totals = new HashMap<>();
        for (ProductoVentaDTO sale : sales) {
            totals.merge(sale.productId(), sale.totalVendido(), Integer::sum);
        }
        return totals;
    }

    // Calcula el remanente total por producto sumando todas las heladeras
    // Retorna un Map: productId → cantidad total remanente
    public static Map<Long, Integer> calculateTotalRemainder(List<FridgeRemainderDTO> fridges) {
        Map<Long, Integer> totals = new HashMap<>();

        // Recorro todas las heladeras y sumo el remanente por producto
        for (FridgeRemainderDTO fridge : fridges) {
            List<ProductRemainderDTO> products = fridge.products();
            for (ProductRemainderDTO product : products) {
                Long productId = product.productId();
                int current = totals.containsKey(productId) ? totals.get(productId) : 0;
                totals.put(productId, current + product.quantity());
            }
        }
        return totals;
    }

    // Calcula la cantidad sugerida a producir por producto
    // suggestedQuantity = total vendido - remanente total
    // Solo incluye productos con suggestedQuantity > 0
    // Retorna un Map: productId → cantidad sugerida
    public static Map<Long, Integer> calculateSuggestedQuantities(Map<Long, Integer> totals, Map<Long, Integer> remainders) {
        Map<Long, Integer> result = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : totals.entrySet()) {
            Long productId = entry.getKey();
            int total = entry.getValue();
            int remainder = remainders.getOrDefault(productId, 0);
            int suggested = total - remainder;
            if (suggested > 0) {
                result.put(productId, suggested);
            }
        }
        return result;
    }
}