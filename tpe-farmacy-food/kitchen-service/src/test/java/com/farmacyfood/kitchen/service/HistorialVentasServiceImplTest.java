package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import com.farmacyfood.kitchen.entity.mongo.VentaHistorica;
import com.farmacyfood.kitchen.repository.VentaHistoricaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistorialVentasServiceImplTest {

    @Mock
    private VentaHistoricaRepository ventaHistoricaRepository;

    @InjectMocks
    private HistorialVentasServiceImpl historialVentasService;

    @Test
    void getVentas_returnsAllWhenNoFilters() {
        List<VentaHistorica> ventas = List.of(
            VentaHistorica.builder().id("1").productId(101L).productName("Ensalada").fridgeId(1L).quantity(10).totalAmount(new BigDecimal("5000")).date(LocalDate.now()).build()
        );
        when(ventaHistoricaRepository.findAll()).thenReturn(ventas);

        List<VentaHistoricaResponseDTO> result = historialVentasService.getVentas(null, null, null, null);

        assertEquals(1, result.size());
        assertEquals(101L, result.get(0).productId());
    }

    @Test
    void getVentas_filtersByProductId() {
        List<VentaHistorica> ventas = List.of(
            VentaHistorica.builder().id("1").productId(101L).productName("Ensalada").fridgeId(1L).quantity(10).totalAmount(new BigDecimal("5000")).date(LocalDate.now()).build()
        );
        when(ventaHistoricaRepository.findByProductId(101L)).thenReturn(ventas);

        List<VentaHistoricaResponseDTO> result = historialVentasService.getVentas(null, null, 101L, null);

        assertEquals(1, result.size());
        verify(ventaHistoricaRepository).findByProductId(101L);
    }

    @Test
    void getVentas_filtersByDateRange() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();
        when(ventaHistoricaRepository.findByDateRange(from, to)).thenReturn(List.of());

        List<VentaHistoricaResponseDTO> result = historialVentasService.getVentas(from, to, null, null);

        assertTrue(result.isEmpty());
        verify(ventaHistoricaRepository).findByDateRange(from, to);
    }
}
