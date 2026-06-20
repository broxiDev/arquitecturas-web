package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// DTO que representa un producto individual con su cantidad remanente en una heladera
@Schema(description = "Producto con cantidad remanente en heladera")
public record ProductRemainderDTO(
    @Schema(description = "ID del producto", example = "101")
    Long productId,

    @Schema(description = "Nombre del producto", example = "Brownie de Chocolate")
    String productName,

    @Schema(description = "Cantidad remanente", example = "3")
    Integer quantity
) {}