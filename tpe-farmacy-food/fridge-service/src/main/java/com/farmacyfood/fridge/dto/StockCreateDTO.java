package com.farmacyfood.fridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para agregar stock a una heladera")
public record StockCreateDTO(
    @Schema(description = "ID del producto en product-service", example = "101")
    @NotNull Long productId,

    @Schema(description = "Cantidad inicial", example = "20")
    @NotNull @Min(0) Integer quantity
) {}
