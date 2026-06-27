package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud de creacion de cocina fantasma")
public record CocinaCreateDTO(
    @Schema(description = "Nombre de la cocina", example = "La mejor cocina veggy de Rosi")
    @NotBlank String nombre,

    @Schema(description = "ID del usuario propietario en user-service", example = "11")
    @NotNull Long usuarioId
) {}
