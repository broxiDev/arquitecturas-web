package com.farmacyfood.kitchen.controller;

import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import com.farmacyfood.kitchen.service.HistorialVentasService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HistorialVentasController.class)
class HistorialVentasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HistorialVentasService historialVentasService;

    @Test
    void getHistorial_returnsAll() throws Exception {
        List<VentaHistoricaResponseDTO> ventas = List.of(
            new VentaHistoricaResponseDTO(101L, "Ensalada", 1L, 10, new BigDecimal("5000"), LocalDate.now())
        );
        when(historialVentasService.getVentas(null, null, null, null)).thenReturn(ventas);

        mockMvc.perform(get("/api/v1/cocina/historial-ventas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].productId").value(101));
    }

    @Test
    void getHistorial_withFilters() throws Exception {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();
        List<VentaHistoricaResponseDTO> ventas = List.of(
            new VentaHistoricaResponseDTO(101L, "Ensalada", 1L, 10, new BigDecimal("5000"), LocalDate.now())
        );
        when(historialVentasService.getVentas(from, to, 101L, null)).thenReturn(ventas);

        mockMvc.perform(get("/api/v1/cocina/historial-ventas")
                .param("from", from.toString())
                .param("to", to.toString())
                .param("productId", "101"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].productName").value("Ensalada"));
    }

    @Test
    void getHistorial_returnsEmptyList() throws Exception {
        when(historialVentasService.getVentas(null, null, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/cocina/historial-ventas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }
}
