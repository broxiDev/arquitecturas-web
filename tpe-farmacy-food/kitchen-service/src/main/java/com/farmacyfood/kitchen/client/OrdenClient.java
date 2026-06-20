package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.ProductoVentaDTO;
import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface OrdenClient {
    List<VentaHistoricaResponseDTO> getVentasRecientes(LocalDate desde, LocalDate hasta);

    // Obtiene las ventas de una cocina fantasma específica en un rango de fechas
    List<ProductoVentaDTO> getSalesByKitchen(String cocinaId, LocalDate from, LocalDate to);
}