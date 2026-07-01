package com.farmacyfood.fridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "DTO para actualizar cantidad de stock de un producto en una heladera")
public record StockUpdateDTO(
    @Schema(description = "ID del producto", example = "101")
    @NotNull Long productId,

    @Schema(description = "Nombre del producto", example = "Brownie de Chocolate")
    String productName,

    @Schema(description = "Nueva cantidad (valor absoluto, no delta)", example = "15")
    @NotNull @Min(0) Integer quantity,

    @Schema(description = "Precio del producto", example = "10.00")
    BigDecimal price
) {}
