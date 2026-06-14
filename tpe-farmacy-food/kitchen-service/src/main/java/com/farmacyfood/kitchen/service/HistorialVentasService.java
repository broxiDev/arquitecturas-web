package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface HistorialVentasService {
    List<VentaHistoricaResponseDTO> getVentas(LocalDate from, LocalDate to, Long productId, Long fridgeId);
}
