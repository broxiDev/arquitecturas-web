package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.client.OrdenClient;
import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialVentasServiceImpl implements HistorialVentasService {

    private final OrdenClient ordenClient;

    @Override
    public List<VentaHistoricaResponseDTO> getVentas(LocalDate from, LocalDate to, Long productId, Long fridgeId) {
        return ordenClient.findHistorialVentas(productId, fridgeId, from, to);
    }
}
