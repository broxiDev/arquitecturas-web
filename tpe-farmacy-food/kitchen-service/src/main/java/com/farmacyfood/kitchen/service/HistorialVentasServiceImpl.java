package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.client.OrdenClient;
import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistorialVentasServiceImpl implements HistorialVentasService {

    private final OrdenClient ordenClient;

    @Override
    public List<VentaHistoricaResponseDTO> getVentas(LocalDate from, LocalDate to, Long productId, Long fridgeId) {
        var resp =  ordenClient.findHistorialVentas(productId, fridgeId, from, to);
        log.info("HistorialVentasServiceImpl.getVentas - Filtros: productId={}, fridgeId={}, from={}, to={}", productId, fridgeId, from, to);
        return resp;
    }
}
