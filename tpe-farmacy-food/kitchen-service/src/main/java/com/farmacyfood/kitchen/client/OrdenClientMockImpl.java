package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.ProductoVentaDTO;
import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
public class OrdenClientMockImpl implements OrdenClient {

    @Override
    public List<VentaHistoricaResponseDTO> getVentasRecientes(LocalDate desde, LocalDate hasta) {
        return List.of(
            new VentaHistoricaResponseDTO(101L, "Brownie de Chocolate", 1L, 10, new BigDecimal("75000.00"), LocalDate.now().minusDays(1)),
            new VentaHistoricaResponseDTO(101L, "Brownie de Chocolate", 2L, 8, new BigDecimal("60000.00"), LocalDate.now().minusDays(2)),
            new VentaHistoricaResponseDTO(102L, "Cheesecake", 1L, 7, new BigDecimal("66500.00"), LocalDate.now().minusDays(1)),
            new VentaHistoricaResponseDTO(102L, "Cheesecake", 2L, 5, new BigDecimal("47500.00"), LocalDate.now().minusDays(3)),
            new VentaHistoricaResponseDTO(201L, "Tostada de Palta Sin Gluten", 3L, 12, new BigDecimal("86400.00"), LocalDate.now().minusDays(1)),
            new VentaHistoricaResponseDTO(201L, "Tostada de Palta Sin Gluten", 4L, 9, new BigDecimal("64800.00"), LocalDate.now().minusDays(2)),
            new VentaHistoricaResponseDTO(301L, "Buddha Bowl Vegano", 5L, 12, new BigDecimal("102000.00"), LocalDate.now().minusDays(1)),
            new VentaHistoricaResponseDTO(301L, "Buddha Bowl Vegano", 6L, 8, new BigDecimal("68000.00"), LocalDate.now().minusDays(2))
        );
    }

    // Retorna ventas hardcodeadas para una cocina fantasma específica
    @Override
    public List<ProductoVentaDTO> getSalesByKitchen(String cocinaId, LocalDate from, LocalDate to) {
        List<ProductoVentaDTO> result = new ArrayList<>();

        // Los totales de ventas deben ser mayores que los remanentes en heladeras
        // para que el plan diario tenga cantidades sugeridas positivas
        // Promedio diario = totalVendido / 7 (redondeado hacia arriba)
        // sugerido = promedio - remanente (solo si > 0)
        switch (cocinaId) {
            // COCINA-DULCE: avg=10/7/8, remainder=5/3/5, suggested=5/4/3
            case "COCINA-DULCE" -> {
                result.add(new ProductoVentaDTO(101L, "Brownie de Chocolate", 70, new BigDecimal("525000.00")));
                result.add(new ProductoVentaDTO(102L, "Cheesecake", 49, new BigDecimal("465500.00")));
                result.add(new ProductoVentaDTO(103L, "Tiramisú", 56, new BigDecimal("492800.00")));
            }
            // COCINA-CELIACA: avg=12/8/10, remainder=4/4/5, suggested=8/4/5
            case "COCINA-CELIACA" -> {
                result.add(new ProductoVentaDTO(201L, "Tostada de Palta Sin Gluten", 84, new BigDecimal("604800.00")));
                result.add(new ProductoVentaDTO(202L, "Bowl de Quinoa Sin Gluten", 56, new BigDecimal("548800.00")));
                result.add(new ProductoVentaDTO(203L, "Rolls de Primavera de Arroz", 70, new BigDecimal("455000.00")));
            }
            // COCINA-VEGANA: avg=12/7/9, remainder=5/3/5, suggested=7/4/4
            case "COCINA-VEGANA" -> {
                result.add(new ProductoVentaDTO(301L, "Buddha Bowl Vegano", 84, new BigDecimal("714000.00")));
                result.add(new ProductoVentaDTO(302L, "Salteado de Tofu", 49, new BigDecimal("382200.00")));
                result.add(new ProductoVentaDTO(303L, "Curry de Garbanzos", 63, new BigDecimal("579600.00")));
            }
            default -> {
                result.add(new ProductoVentaDTO(101L, "Producto Genérico 1", 14, new BigDecimal("105000.00")));
                result.add(new ProductoVentaDTO(102L, "Producto Genérico 2", 10, new BigDecimal("95000.00")));
            }
        }

        return result;
    }
}