package com.farmacyfood.fridge.controller;

import com.farmacyfood.fridge.dto.FridgeRemainderDTO;
import com.farmacyfood.fridge.dto.HeladeraCreateDTO;
import com.farmacyfood.fridge.dto.HeladeraResponseDTO;
import com.farmacyfood.fridge.dto.HeladeraUpdateDTO;
import com.farmacyfood.fridge.dto.ProductRemainderDTO;
import com.farmacyfood.fridge.exception.GlobalExceptionHandler;
import com.farmacyfood.fridge.exception.HeladeraNotFoundException;
import com.farmacyfood.fridge.service.HeladeraService;
import com.farmacyfood.fridge.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HeladeraController.class)
@Import(GlobalExceptionHandler.class)
class HeladeraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HeladeraService heladeraService;

    @MockBean
    private StockService stockService;

    @Test
    void findAll_returnsList() throws Exception {
        when(heladeraService.findAll(null, null, null, null)).thenReturn(List.of(
            new HeladeraResponseDTO(1L, "Heladera A", -34.6, -58.4, "Dir A", "ACTIVE", Set.of(1L), null, LocalDateTime.now(), null)
        ));

        mockMvc.perform(get("/api/v1/heladeras"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Heladera A"));
    }

    @Test
    void findById_returnsHeladera() throws Exception {
        when(heladeraService.findById(1L)).thenReturn(
            new HeladeraResponseDTO(1L, "Heladera B", -34.6, -58.4, "Dir B", "ACTIVE", Set.of(1L), null, LocalDateTime.now(), null));

        mockMvc.perform(get("/api/v1/heladeras/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Heladera B"));
    }

    @Test
    void findById_returns404() throws Exception {
        when(heladeraService.findById(99L)).thenThrow(new HeladeraNotFoundException("No existe heladera con id: 99"));

        mockMvc.perform(get("/api/v1/heladeras/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void create_returns201() throws Exception {
        HeladeraResponseDTO created = new HeladeraResponseDTO(1L, "Nueva", -34.6, -58.4, "Dir", "ACTIVE", Set.of(), null, LocalDateTime.now(), null);
        when(heladeraService.create(any(HeladeraCreateDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/heladeras")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Nueva\",\"latitude\":-34.6,\"longitude\":-58.4,\"address\":\"Dir\",\"status\":\"ACTIVE\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_returnsOk() throws Exception {
        HeladeraResponseDTO updated = new HeladeraResponseDTO(1L, "Updated", -34.6, -58.4, "Dir", "MAINTENANCE", Set.of(1L), null, LocalDateTime.now(), null);
        when(heladeraService.update(eq(1L), any(HeladeraUpdateDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/heladeras/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\",\"status\":\"MAINTENANCE\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("MAINTENANCE"));
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/heladeras/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns404WhenNotFound() throws Exception {
        doThrow(new HeladeraNotFoundException("No existe")).when(heladeraService).delete(99L);

        mockMvc.perform(delete("/api/v1/heladeras/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getRemainderByCocina_returnsList() throws Exception {
        when(stockService.getRemainderByCocinaId(1L)).thenReturn(List.of(
            new FridgeRemainderDTO(1L, List.of(
                new ProductRemainderDTO(101L, "Brownie de Chocolate", 3),
                new ProductRemainderDTO(102L, "Cheesecake", 2)
            )),
            new FridgeRemainderDTO(2L, List.of(
                new ProductRemainderDTO(101L, "Brownie de Chocolate", 2)
            ))
        ));

        mockMvc.perform(get("/api/v1/heladeras/cocina/1/remanente"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].fridgeId").value(1))
            .andExpect(jsonPath("$[0].products[0].productName").value("Brownie de Chocolate"))
            .andExpect(jsonPath("$[1].fridgeId").value(2));
    }

    @Test
    void getRemainderByCocina_returnsEmptyList() throws Exception {
        when(stockService.getRemainderByCocinaId(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/heladeras/cocina/99/remanente"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }
}
