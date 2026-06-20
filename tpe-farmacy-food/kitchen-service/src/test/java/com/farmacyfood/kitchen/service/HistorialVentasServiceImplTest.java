package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.client.OrdenClient;
import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistorialVentasServiceImplTest {

    @Mock
    private OrdenClient ordenClient;

    @InjectMocks
    private HistorialVentasServiceImpl historialVentasService;

    @Test
    void getVentas_returnsAllWhenNoFilters() {
        List<VentaHistoricaResponseDTO> ventasEsperadas = new ArrayList<>();
        ventasEsperadas.add(new VentaHistoricaResponseDTO(101L, "Ensalada", 1L, 10, new BigDecimal("5000"), LocalDate.now()));

        when(ordenClient.findHistorialVentas(null, null, null, null)).thenReturn(ventasEsperadas);

        List<VentaHistoricaResponseDTO> result = historialVentasService.getVentas(null, null, null, null);

        assertEquals(1, result.size());
        assertEquals(101L, result.get(0).productId());
        verify(ordenClient).findHistorialVentas(null, null, null, null);
    }

    @Test
    void getVentas_filtersByProductId() {
        List<VentaHistoricaResponseDTO> ventasEsperadas = new ArrayList<>();
        ventasEsperadas.add(new VentaHistoricaResponseDTO(101L, "Ensalada", 1L, 10, new BigDecimal("5000"), LocalDate.now()));

        when(ordenClient.findHistorialVentas(101L, null, null, null)).thenReturn(ventasEsperadas);

        List<VentaHistoricaResponseDTO> result = historialVentasService.getVentas(null, null, 101L, null);

        assertEquals(1, result.size());
        verify(ordenClient).findHistorialVentas(101L, null, null, null);
    }

    @Test
    void getVentas_filtersByDateRange() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        when(ordenClient.findHistorialVentas(null, null, from, to)).thenReturn(new ArrayList<>());

        List<VentaHistoricaResponseDTO> result = historialVentasService.getVentas(from, to, null, null);

        assertEquals(0, result.size());
        verify(ordenClient).findHistorialVentas(null, null, from, to);
    }

    @Test
    void getVentas_filtersByFridgeId() {
        List<VentaHistoricaResponseDTO> ventasEsperadas = new ArrayList<>();
        ventasEsperadas.add(new VentaHistoricaResponseDTO(101L, "Ensalada", 1L, 10, new BigDecimal("5000"), LocalDate.now()));

        when(ordenClient.findHistorialVentas(null, 1L, null, null)).thenReturn(ventasEsperadas);

        List<VentaHistoricaResponseDTO> result = historialVentasService.getVentas(null, null, null, 1L);

        assertEquals(1, result.size());
        verify(ordenClient).findHistorialVentas(null, 1L, null, null);
    }

    @Test
    void getVentas_filtersByAll() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        List<VentaHistoricaResponseDTO> ventasEsperadas = new ArrayList<>();
        ventasEsperadas.add(new VentaHistoricaResponseDTO(101L, "Ensalada", 1L, 10, new BigDecimal("5000"), LocalDate.now()));

        when(ordenClient.findHistorialVentas(101L, 1L, from, to)).thenReturn(ventasEsperadas);

        List<VentaHistoricaResponseDTO> result = historialVentasService.getVentas(from, to, 101L, 1L);

        assertEquals(1, result.size());
        verify(ordenClient).findHistorialVentas(101L, 1L, from, to);
    }
}
