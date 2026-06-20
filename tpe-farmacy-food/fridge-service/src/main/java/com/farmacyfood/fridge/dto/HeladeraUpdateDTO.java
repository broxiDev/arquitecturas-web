package com.farmacyfood.fridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para actualizar una heladera (todos los campos son opcionales)")
public record HeladeraUpdateDTO(
    @Schema(description = "Nombre de la heladera", example = "Heladera Recoleta")
    String name,

    @Schema(description = "Latitud", example = "-34.5875")
    Double latitude,

    @Schema(description = "Longitud", example = "-58.3924")
    Double longitude,

    @Schema(description = "Dirección", example = "Av. Quintana 123, Recoleta")
    String address,

    @Schema(description = "Estado", example = "MAINTENANCE")
    String status,

    @Schema(description = "ID de la cocina asociada", example = "COCINA-VEGANA")
    String cocinaId
) {}
