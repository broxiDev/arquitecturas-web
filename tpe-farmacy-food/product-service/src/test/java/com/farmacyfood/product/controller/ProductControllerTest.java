package com.farmacyfood.product.controller;

import com.farmacyfood.product.dto.ProductRequest;
import com.farmacyfood.product.dto.ProductResponse;
import com.farmacyfood.product.service.CatalogoService;
import com.farmacyfood.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private CatalogoService catalogoService;

    @Test
    void createProductInCatalog_whenValidRequest_returns201() throws Exception {
        ProductRequest request = new ProductRequest(
                "Ensalada Test",
                "Descripción test",
                "VEGANO",
                new BigDecimal("8500.00"),
                "/images/test.jpg",
                "Calorías: 350kcal",
                new BigDecimal("4.00")
        );

        ProductResponse response = new ProductResponse(
                1L,
                "Ensalada Test",
                "Descripción test",
                "VEGANO",
                new BigDecimal("8500.00"),
                "/images/test.jpg",
                "Calorías: 350kcal",
                new BigDecimal("4.00"),
                "cocina-sur",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(catalogoService.createOrUpdateProduct(eq("cocina-sur"), any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/productos/cocina/cocina-sur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ensalada Test"))
                .andExpect(jsonPath("$.dietaryCategory").value("VEGANO"))
                .andExpect(jsonPath("$.cocinaId").value("cocina-sur"));
    }

    @Test
    void getProductsByCocina_whenKitchenExists_returnsProductList() throws Exception {
        ProductResponse response = new ProductResponse(
                1L,
                "Ensalada Test",
                "Descripción test",
                "VEGANO",
                new BigDecimal("8500.00"),
                "/images/test.jpg",
                "Calorías: 350kcal",
                new BigDecimal("4.00"),
                "cocina-sur",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(catalogoService.getProductsByCocina("cocina-sur")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/productos/cocina/cocina-sur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ensalada Test"))
                .andExpect(jsonPath("$[0].cocinaId").value("cocina-sur"));
    }

    @Test
    void getProductsByCocina_whenNoProducts_returnsEmptyList() throws Exception {
        when(catalogoService.getProductsByCocina("cocina-vacia")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/productos/cocina/cocina-vacia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createProductInCatalog_whenMissingName_returns400() throws Exception {
        ProductRequest request = new ProductRequest(
                null,
                "test",
                "VEGANO",
                new BigDecimal("8500.00"),
                null,
                null,
                null
        );

        when(catalogoService.createOrUpdateProduct(eq("cocina-sur"), any(ProductRequest.class)))
                .thenThrow(new com.farmacyfood.product.exception.InvalidProductDataException("El nombre del producto es obligatorio"));

        mockMvc.perform(post("/api/v1/productos/cocina/cocina-sur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
