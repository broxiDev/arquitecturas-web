package com.farmacyfood.fridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta con datos de stock de un producto en una heladera")
public record StockResponseDTO(
    @Schema(description = "ID del registro de stock", example = "1")
    Long id,

    @Schema(description = "ID de la heladera", example = "1")
    Long fridgeId,

    @Schema(description = "ID del producto", example = "101")
    Long productId,

    @Schema(description = "Cantidad disponible", example = "15")
    Integer quantity,

    @Schema(description = "Última actualización", example = "2026-06-14T10:00:00")
    LocalDateTime updatedAt
) {}
