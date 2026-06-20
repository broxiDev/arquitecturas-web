package com.farmacyfood.fridge.controller;

import com.farmacyfood.fridge.dto.StockResponseDTO;
import com.farmacyfood.fridge.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Test
    void getStock_returnsList() throws Exception {
        when(stockService.getStockByHeladera(1L)).thenReturn(List.of(
            new StockResponseDTO(1L, 1L, 101L, "Brownie de Chocolate", 10, LocalDateTime.now())
        ));

        mockMvc.perform(get("/api/v1/heladeras/1/stock"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].productId").value(101))
            .andExpect(jsonPath("$[0].productName").value("Brownie de Chocolate"))
            .andExpect(jsonPath("$[0].quantity").value(10));
    }

    @Test
    void addStock_returns201() throws Exception {
        when(stockService.addStock(eq(1L), any())).thenReturn(
            new StockResponseDTO(1L, 1L, 101L, "Brownie de Chocolate", 20, LocalDateTime.now()));

        mockMvc.perform(post("/api/v1/heladeras/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":101,\"productName\":\"Brownie de Chocolate\",\"quantity\":20}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.productId").value(101));
    }

    @Test
    void updateStock_returnsOk() throws Exception {
        when(stockService.updateStock(eq(1L), any())).thenReturn(
            new StockResponseDTO(1L, 1L, 101L, "Brownie de Chocolate", 15, LocalDateTime.now()));

        mockMvc.perform(put("/api/v1/heladeras/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":101,\"quantity\":15}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantity").value(15));
    }
}
