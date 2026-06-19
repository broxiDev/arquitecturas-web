package com.farmacyfood.recommendation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(example = "{\"userId\":1,\"productos\":[{\"productId\":301,\"productName\":\"Ensalada Vegana Premium\",\"reason\":\"Producto recomendado basado en tus preferencias dietarias\",\"dietaryCategory\":\"VEGANO\"},{\"productId\":302,\"productName\":\"Bowl Vegano Energético\",\"reason\":\"Producto recomendado basado en tus preferencias dietarias\",\"dietaryCategory\":\"VEGANO\"}],\"generatedAt\":\"2026-06-19T00:30:00\"}")
public record RecomendacionResponseDTO(
        @Schema(example = "1") Long userId,
        List<ProductoRecomendadoDTO> productos,
        @Schema(example = "2026-06-19T00:30:00") LocalDateTime generatedAt
) {}
