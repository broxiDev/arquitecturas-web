package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud de creacion de cocina fantasma")
public record CocinaCreateDTO(
    @Schema(description = "Nombre de la cocina", example = "La mejor cocina veggy de Rosi")
    @NotBlank String nombre
) {}
