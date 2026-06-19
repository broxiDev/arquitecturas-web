package com.farmacyfood.recommendation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductoRecomendadoDTO(
        @Schema(example = "301") Long productId,
        @Schema(example = "Ensalada Vegana Premium") String productName,
        @Schema(example = "Producto recomendado basado en tus preferencias dietarias") String reason,
        @Schema(example = "VEGANO") String dietaryCategory
) {}
