package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud de registro de producto en el catálogo local de una cocina")
public record CatalogoLocalRequestDTO(
    @Schema(description = "ID del producto", example = "101")
    @NotNull Long productId,

    @Schema(description = "Nombre del producto", example = "Lechuga y tomate")
    @NotBlank String productName
) {}
