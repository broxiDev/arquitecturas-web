package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import com.farmacyfood.kitchen.entity.mongo.VentaHistorica;
import com.farmacyfood.kitchen.repository.VentaHistoricaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialVentasServiceImpl implements HistorialVentasService {

    private final VentaHistoricaRepository ventaHistoricaRepository;

    @Override
    public List<VentaHistoricaResponseDTO> getVentas(LocalDate from, LocalDate to, Long productId, Long fridgeId) {
        List<VentaHistorica> ventas;

        if (productId != null && from != null && to != null) {
            ventas = ventaHistoricaRepository.findByProductIdAndDateRange(productId, from, to);
        } else if (fridgeId != null && from != null && to != null) {
            ventas = ventaHistoricaRepository.findByFridgeIdAndDateRange(fridgeId, from, to);
        } else if (productId != null) {
            ventas = ventaHistoricaRepository.findByProductId(productId);
        } else if (fridgeId != null) {
            ventas = ventaHistoricaRepository.findByFridgeId(fridgeId);
        } else if (from != null && to != null) {
            ventas = ventaHistoricaRepository.findByDateRange(from, to);
        } else {
            ventas = ventaHistoricaRepository.findAll();
        }

        return ventas.stream()
            .map(this::toDTO)
            .toList();
    }

    private VentaHistoricaResponseDTO toDTO(VentaHistorica v) {
        return new VentaHistoricaResponseDTO(
            v.getProductId(),
            v.getProductName(),
            v.getFridgeId(),
            v.getQuantity(),
            v.getTotalAmount(),
            v.getDate()
        );
    }
}
