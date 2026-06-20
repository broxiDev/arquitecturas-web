package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item del plan diario de producción de la cocina fantasma")
public record ItemPlanDTO(
    @Schema(description = "ID del producto", example = "101")
    Long productId,

    @Schema(description = "Nombre del producto", example = "Brownie de Chocolate")
    String productName,

    @Schema(description = "Cantidad sugerida a producir", example = "10")
    Integer suggestedQuantity
) {}
