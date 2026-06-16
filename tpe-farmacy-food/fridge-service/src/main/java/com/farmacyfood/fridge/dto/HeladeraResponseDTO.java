package com.farmacyfood.fridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Respuesta con datos de una heladera")
public record HeladeraResponseDTO(
    @Schema(description = "ID de la heladera", example = "1")
    Long id,

    @Schema(description = "Nombre de la heladera", example = "Heladera Palermo")
    String name,

    @Schema(description = "Latitud", example = "-34.6037")
    Double latitude,

    @Schema(description = "Longitud", example = "-58.3816")
    Double longitude,

    @Schema(description = "Dirección", example = "Av. Santa Fe 1234, Palermo")
    String address,

    @Schema(description = "Estado operativo", example = "ACTIVE")
    String status,

    @Schema(description = "Último mantenimiento", example = "2026-06-01")
    LocalDate lastMaintenance,

    @Schema(description = "Fecha de creación", example = "2026-06-14T08:00:00")
    LocalDateTime createdAt,

    @Schema(description = "Fecha de última actualización", example = "2026-06-14T10:00:00")
    LocalDateTime updatedAt
) {}
