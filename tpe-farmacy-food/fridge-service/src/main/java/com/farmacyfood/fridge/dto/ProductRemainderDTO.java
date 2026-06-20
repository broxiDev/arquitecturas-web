package com.farmacyfood.fridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Producto con cantidad remanente en una heladera")
public record ProductRemainderDTO(
    @Schema(description = "ID del producto", example = "101")
    Long productId,

    @Schema(description = "Nombre del producto", example = "Brownie de Chocolate")
    String productName,

    @Schema(description = "Cantidad remanente", example = "3")
    Integer quantity
) {}
