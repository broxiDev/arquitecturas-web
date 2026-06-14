package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface OrdenClient {
    List<VentaHistoricaResponseDTO> getVentasRecientes(LocalDate desde, LocalDate hasta);
}
