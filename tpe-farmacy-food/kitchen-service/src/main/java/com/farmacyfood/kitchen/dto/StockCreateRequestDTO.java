package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "DTO que se envia a fridge-service para agregar stock a una heladera")
public record StockCreateRequestDTO(
    @Schema(description = "ID del producto", example = "101")
    @NotNull Long productId,

    @Schema(description = "Nombre del producto", example = "Lechuga y tomate")
    @NotBlank String productName,

    @Schema(description = "Cantidad inicial", example = "10")
    @NotNull @Min(0) Integer quantity,

    @Schema(description = "Precio del producto", example = "10.00")
    @NotNull BigDecimal price
) {}
