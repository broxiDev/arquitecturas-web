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

    // Catalogos mock: 1=dulce, 2=celiaca, 3=vegana
    private static final Long COCINA_DULCE = 1L;
    private static final Long COCINA_CELIACA = 2L;
    private static final Long COCINA_VEGANA = 3L;

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

    @Override
    public List<ProductoVentaDTO> getSalesByKitchen(Long cocinaId, LocalDate from, LocalDate to) {
        List<ProductoVentaDTO> result = new ArrayList<>();

        if (COCINA_DULCE.equals(cocinaId)) {
            result.add(new ProductoVentaDTO(101L, "Brownie de Chocolate", 70, new BigDecimal("525000.00")));
            result.add(new ProductoVentaDTO(102L, "Cheesecake", 49, new BigDecimal("465500.00")));
            result.add(new ProductoVentaDTO(103L, "Tiramisu", 56, new BigDecimal("492800.00")));
        } else if (COCINA_CELIACA.equals(cocinaId)) {
            result.add(new ProductoVentaDTO(201L, "Tostada de Palta Sin Gluten", 84, new BigDecimal("604800.00")));
            result.add(new ProductoVentaDTO(202L, "Bowl de Quinoa Sin Gluten", 56, new BigDecimal("548800.00")));
            result.add(new ProductoVentaDTO(203L, "Rolls de Primavera de Arroz", 70, new BigDecimal("455000.00")));
        } else if (COCINA_VEGANA.equals(cocinaId)) {
            result.add(new ProductoVentaDTO(301L, "Buddha Bowl Vegano", 84, new BigDecimal("714000.00")));
            result.add(new ProductoVentaDTO(302L, "Salteado de Tofu", 49, new BigDecimal("382200.00")));
            result.add(new ProductoVentaDTO(303L, "Curry de Garbanzos", 63, new BigDecimal("579600.00")));
        } else {
            result.add(new ProductoVentaDTO(101L, "Producto Generico 1", 14, new BigDecimal("105000.00")));
            result.add(new ProductoVentaDTO(102L, "Producto Generico 2", 10, new BigDecimal("95000.00")));
        }

        return result;
    }

    @Override
    public List<VentaHistoricaResponseDTO> findHistorialVentas(Long productId, Long fridgeId, LocalDate from, LocalDate to) {
        List<VentaHistoricaResponseDTO> all = new ArrayList<>();

        all.add(new VentaHistoricaResponseDTO(101L, "Brownie de Chocolate", 1L, 10, new BigDecimal("75000.00"), LocalDate.of(2026, 6, 19)));
        all.add(new VentaHistoricaResponseDTO(101L, "Brownie de Chocolate", 2L, 8, new BigDecimal("60000.00"), LocalDate.of(2026, 6, 18)));
        all.add(new VentaHistoricaResponseDTO(102L, "Cheesecake", 1L, 7, new BigDecimal("66500.00"), LocalDate.of(2026, 6, 19)));
        all.add(new VentaHistoricaResponseDTO(102L, "Cheesecake", 2L, 5, new BigDecimal("47500.00"), LocalDate.of(2026, 6, 17)));
        all.add(new VentaHistoricaResponseDTO(103L, "Tiramisu", 1L, 12, new BigDecimal("105600.00"), LocalDate.of(2026, 6, 18)));
        all.add(new VentaHistoricaResponseDTO(103L, "Tiramisu", 2L, 9, new BigDecimal("79200.00"), LocalDate.of(2026, 6, 20)));
        all.add(new VentaHistoricaResponseDTO(201L, "Tostada de Palta Sin Gluten", 3L, 12, new BigDecimal("86400.00"), LocalDate.of(2026, 6, 19)));
        all.add(new VentaHistoricaResponseDTO(201L, "Tostada de Palta Sin Gluten", 4L, 9, new BigDecimal("64800.00"), LocalDate.of(2026, 6, 18)));
        all.add(new VentaHistoricaResponseDTO(202L, "Bowl de Quinoa Sin Gluten", 3L, 8, new BigDecimal("78400.00"), LocalDate.of(2026, 6, 20)));
        all.add(new VentaHistoricaResponseDTO(202L, "Bowl de Quinoa Sin Gluten", 4L, 6, new BigDecimal("58800.00"), LocalDate.of(2026, 6, 17)));
        all.add(new VentaHistoricaResponseDTO(203L, "Rolls de Primavera de Arroz", 3L, 10, new BigDecimal("65000.00"), LocalDate.of(2026, 6, 19)));
        all.add(new VentaHistoricaResponseDTO(203L, "Rolls de Primavera de Arroz", 4L, 7, new BigDecimal("45500.00"), LocalDate.of(2026, 6, 18)));
        all.add(new VentaHistoricaResponseDTO(301L, "Buddha Bowl Vegano", 5L, 12, new BigDecimal("102000.00"), LocalDate.of(2026, 6, 19)));
        all.add(new VentaHistoricaResponseDTO(301L, "Buddha Bowl Vegano", 6L, 8, new BigDecimal("68000.00"), LocalDate.of(2026, 6, 18)));
        all.add(new VentaHistoricaResponseDTO(302L, "Salteado de Tofu", 5L, 7, new BigDecimal("54600.00"), LocalDate.of(2026, 6, 20)));
        all.add(new VentaHistoricaResponseDTO(302L, "Salteado de Tofu", 6L, 5, new BigDecimal("39000.00"), LocalDate.of(2026, 6, 17)));
        all.add(new VentaHistoricaResponseDTO(303L, "Curry de Garbanzos", 5L, 9, new BigDecimal("82800.00"), LocalDate.of(2026, 6, 19)));
        all.add(new VentaHistoricaResponseDTO(303L, "Curry de Garbanzos", 6L, 6, new BigDecimal("55200.00"), LocalDate.of(2026, 6, 18)));

        List<VentaHistoricaResponseDTO> filtered = new ArrayList<>();
        for (VentaHistoricaResponseDTO v : all) {
            boolean matches = true;
            if (productId != null && !v.productId().equals(productId)) {
                matches = false;
            }
            if (fridgeId != null && !v.fridgeId().equals(fridgeId)) {
                matches = false;
            }
            if (from != null && v.date().isBefore(from)) {
                matches = false;
            }
            if (to != null && v.date().isAfter(to)) {
                matches = false;
            }
            if (matches) {
                filtered.add(v);
            }
        }

        return filtered;
    }
}
