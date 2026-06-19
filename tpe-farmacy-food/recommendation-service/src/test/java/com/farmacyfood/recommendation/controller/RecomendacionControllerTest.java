package com.farmacyfood.recommendation.controller;

import com.farmacyfood.recommendation.dto.ProductoRecomendadoDTO;
import com.farmacyfood.recommendation.dto.RecomendacionResponseDTO;
import com.farmacyfood.recommendation.service.RecomendacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecomendacionController.class)
class RecomendacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecomendacionService recomendacionService;

    @Test
    void getRecomendaciones_retornaOkConBodyValido() throws Exception {
        Long userId = 1L;
        List<ProductoRecomendadoDTO> productos = new ArrayList<>();
        productos.add(new ProductoRecomendadoDTO(101L, "Producto Test", "Razón test", "VEGANO"));
        RecomendacionResponseDTO response = new RecomendacionResponseDTO(userId, productos, LocalDateTime.now());

        when(recomendacionService.getRecomendaciones(userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/recomendaciones/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.productos").isArray())
                .andExpect(jsonPath("$.productos[0].productName").value("Producto Test"));
    }

    @Test
    void getRecomendaciones_retornaListaVaciaCuandoNoHayRecomendaciones() throws Exception {
        Long userId = 99L;
        RecomendacionResponseDTO response = new RecomendacionResponseDTO(userId, new ArrayList<>(), LocalDateTime.now());

        when(recomendacionService.getRecomendaciones(userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/recomendaciones/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.productos").isArray())
                .andExpect(jsonPath("$.productos").isEmpty());
    }
}
