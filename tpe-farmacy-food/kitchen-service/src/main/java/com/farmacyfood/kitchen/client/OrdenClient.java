package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.ProductoVentaDTO;
import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface OrdenClient {
    List<VentaHistoricaResponseDTO> getVentasRecientes(LocalDate desde, LocalDate hasta);

    List<ProductoVentaDTO> getSalesByKitchen(Long cocinaId, LocalDate from, LocalDate to);

    List<VentaHistoricaResponseDTO> findHistorialVentas(Long productId, Long fridgeId, LocalDate from, LocalDate to);
}
