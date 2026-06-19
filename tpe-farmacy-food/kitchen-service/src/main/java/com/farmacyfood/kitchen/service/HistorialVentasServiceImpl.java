package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import com.farmacyfood.kitchen.entity.mongo.VentaHistorica;
import com.farmacyfood.kitchen.repository.VentaHistoricaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialVentasServiceImpl implements HistorialVentasService {

    private final VentaHistoricaRepository ventaHistoricaRepository;

    @Override
    public List<VentaHistoricaResponseDTO> getVentas(LocalDate from, LocalDate to, Long productId, Long fridgeId) {
        // Busco las ventas en Mongo según los filtros que lleguen.
        // Se prioriza la combinación más específica primero (producto+fechas, heladera+fechas)
        // y después los filtros individuales.
        List<VentaHistorica> ventas = buscarVentas(from, to, productId, fridgeId);

        // Convierto las entidades a DTOs para la respuesta
        List<VentaHistoricaResponseDTO> resultado = new ArrayList<>();
        for (VentaHistorica v : ventas) {
            resultado.add(toDTO(v));
        }
        return resultado;
    }

    // Resuelve qué query ejecutar según los filtros que lleguen (pueden venir null)
    private List<VentaHistorica> buscarVentas(LocalDate from, LocalDate to, Long productId, Long fridgeId) {
        if (productId != null && from != null && to != null) {
            return ventaHistoricaRepository.findByProductIdAndDateRange(productId, from, to);
        } else if (fridgeId != null && from != null && to != null) {
            return ventaHistoricaRepository.findByFridgeIdAndDateRange(fridgeId, from, to);
        } else if (productId != null) {
            return ventaHistoricaRepository.findByProductId(productId);
        } else if (fridgeId != null) {
            return ventaHistoricaRepository.findByFridgeId(fridgeId);
        } else if (from != null && to != null) {
            return ventaHistoricaRepository.findByDateRange(from, to);
        } else {
            // Si no llega ningún filtro, traigo todo
            return ventaHistoricaRepository.findAll();
        }
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
