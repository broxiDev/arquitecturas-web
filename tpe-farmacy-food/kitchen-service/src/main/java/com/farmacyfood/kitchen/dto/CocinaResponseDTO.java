package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Cocina fantasma registrada")
public record CocinaResponseDTO(
    @Schema(description = "ID de la cocina (cocinaId)", example = "1")
    Long id,

    @Schema(description = "Nombre de la cocina", example = "La mejor cocina veggy de Rosi")
    String nombre,

    @Schema(description = "Username (auth) del usuario propietario", example = "auth_juan")
    String usuario,

    @Schema(description = "Fecha de creacion", example = "2026-06-27T14:00:00")
    LocalDateTime createdAt
) {}
