package com.farmacyfood.kitchen.controller;

import com.farmacyfood.kitchen.dto.ItemPlanDTO;
import com.farmacyfood.kitchen.dto.PlanDiarioResponseDTO;
import com.farmacyfood.kitchen.exception.GlobalExceptionHandler;
import com.farmacyfood.kitchen.exception.PlanNotFoundException;
import com.farmacyfood.kitchen.service.PlanDiarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlanDiarioController.class)
@Import(GlobalExceptionHandler.class)
class PlanDiarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanDiarioService planDiarioService;

    private static final Long COCINA_ID = 1L;

    @Test
    void getPlan_returnsOk() throws Exception {
        LocalDate today = LocalDate.now();
        PlanDiarioResponseDTO dto = new PlanDiarioResponseDTO(1L, today, COCINA_ID,
            List.of(new ItemPlanDTO(101L, "Brownie de Chocolate", 10)), LocalDateTime.now());

        when(planDiarioService.getPlanByDate(today, COCINA_ID)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/cocina/plan-diario").param("cocinaId", String.valueOf(COCINA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.date").value(today.toString()))
            .andExpect(jsonPath("$.cocinaId").value(COCINA_ID.intValue()))
            .andExpect(jsonPath("$.items[0].productId").value(101));
    }

    @Test
    void getPlan_withFechaParam() throws Exception {
        LocalDate fecha = LocalDate.of(2026, 6, 15);
        PlanDiarioResponseDTO dto = new PlanDiarioResponseDTO(2L, fecha, COCINA_ID,
            List.of(new ItemPlanDTO(102L, "Cheesecake", 5)), LocalDateTime.now());

        when(planDiarioService.getPlanByDate(fecha, COCINA_ID)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/cocina/plan-diario")
                .param("cocinaId", String.valueOf(COCINA_ID))
                .param("fecha", "2026-06-15"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.date").value("2026-06-15"));
    }

    @Test
    void getPlan_returns404WhenNotFound() throws Exception {
        LocalDate today = LocalDate.now();
        when(planDiarioService.getPlanByDate(today, COCINA_ID)).thenThrow(new PlanNotFoundException("No existe plan"));

        mockMvc.perform(get("/api/v1/cocina/plan-diario").param("cocinaId", String.valueOf(COCINA_ID)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void generarPlan_returnsOk() throws Exception {
        LocalDate today = LocalDate.now();
        PlanDiarioResponseDTO dto = new PlanDiarioResponseDTO(1L, today, COCINA_ID,
            List.of(new ItemPlanDTO(101L, "Brownie de Chocolate", 8)), LocalDateTime.now());

        when(planDiarioService.generarPlan(today, COCINA_ID)).thenReturn(dto);

        mockMvc.perform(post("/api/v1/cocina/plan-diario").param("cocinaId", String.valueOf(COCINA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }
}
