package com.farmacyfood.fridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "DTO para agregar stock a una heladera")
public record StockCreateDTO(
    @Schema(description = "ID del producto", example = "101")
    @NotNull Long productId,

    @Schema(description = "Nombre del producto", example = "Brownie de Chocolate")
    @NotBlank String productName,

    @Schema(description = "Cantidad inicial", example = "20")
    @NotNull @Min(0) Integer quantity,

    @Schema(description = "ID de la cocina a la que pertenece el producto", example = "1")
    @NotNull Long cocinaId,

    @Schema(description = "Precio del producto", example = "10.00")
    @NotNull @DecimalMin("0.00") BigDecimal price
) {}
