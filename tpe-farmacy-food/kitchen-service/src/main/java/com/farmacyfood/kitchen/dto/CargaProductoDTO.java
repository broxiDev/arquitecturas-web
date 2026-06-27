package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Producto a cargar en una heladera")
public record CargaProductoDTO(
    @Schema(description = "ID del producto", example = "101")
    @NotNull Long productId,

    @Schema(description = "Nombre del producto", example = "Lechuga y tomate")
    @NotBlank String productName,

    @Schema(description = "Cantidad a cargar", example = "10")
    @NotNull @Min(1) Integer quantity,

    @Schema(description = "Precio del producto", example = "10.00")
    @NotNull BigDecimal price
) {}
