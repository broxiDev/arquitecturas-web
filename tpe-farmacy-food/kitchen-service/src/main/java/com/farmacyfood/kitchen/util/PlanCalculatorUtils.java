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

    // Calcula el promedio diario de ventas por producto (redondeado para arriba)
    // historyDays es la cantidad de días del período histórico (ej: 7)
    // Retorna un Map: productId → ceiling(promedio diario)
    public static Map<Long, Integer> calculateDailyAverage(List<ProductoVentaDTO> sales, int historyDays) {
        Map<Long, Integer> sums = new HashMap<>();
        Map<Long, String> productNames = new HashMap<>();

        // Sumo las cantidades vendidas por producto
        for (ProductoVentaDTO sale : sales) {
            Long productId = sale.productId();
            int current = sums.containsKey(productId) ? sums.get(productId) : 0;
            sums.put(productId, current + sale.totalVendido());
            productNames.put(productId, sale.productName());
        }

        // Calculo el promedio redondeado para arriba
        Map<Long, Integer> averages = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : sums.entrySet()) {
            Long productId = entry.getKey();
            int totalSold = entry.getValue();
            // Promedio = total / días del período, redondeado para arriba
            int average = (int) Math.ceil((double) totalSold / historyDays);
            averages.put(productId, average);
        }
        return averages;
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
    // suggestedQuantity = ceiling(promedio) - remanente total
    // Solo incluye productos con suggestedQuantity > 0
    // Retorna un Map: productId → cantidad sugerida
    public static Map<Long, Integer> calculateSuggestedQuantities(Map<Long, Integer> averages, Map<Long, Integer> remainders) {
        Map<Long, Integer> result = new HashMap<>();

        // Recorro todos los productos que tienen promedio calculado
        for (Map.Entry<Long, Integer> entry : averages.entrySet()) {
            Long productId = entry.getKey();
            int average = entry.getValue();

            // Obtengo el remanente total para este producto (0 si no hay)
            int remainder = 0;
            if (remainders.containsKey(productId)) {
                remainder = remainders.get(productId);
            }

            // Cantidad sugerida = promedio - remanente
            int suggested = average - remainder;

            // Solo incluyo si la cantidad sugerida es mayor a 0
            if (suggested > 0) {
                result.put(productId, suggested);
            }
        }
        return result;
    }
}