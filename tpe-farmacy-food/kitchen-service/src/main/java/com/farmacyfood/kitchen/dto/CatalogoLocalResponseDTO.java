package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Producto del catálogo local de una cocina fantasma")
public record CatalogoLocalResponseDTO(
    @Schema(description = "ID del registro en catalogo", example = "1")
    Long id,

    @Schema(description = "ID del producto", example = "101")
    Long productId,

    @Schema(description = "Nombre del producto", example = "Lechuga y tomate")
    String productName,

    @Schema(description = "ID de la cocina", example = "1")
    Long cocinaId
) {}
