package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Plan diario de producción de la cocina fantasma")
public record PlanDiarioResponseDTO(
    @Schema(description = "ID del plan", example = "1")
    Long id,

    @Schema(description = "Fecha del plan", example = "2026-06-14")
    LocalDate date,

    @Schema(description = "ID de la cocina fantasma", example = "COCINA-DULCE")
    String cocinaId,

    @Schema(description = "Lista de productos sugeridos")
    List<ItemPlanDTO> items,

    @Schema(description = "Fecha de creación del plan", example = "2026-06-14T08:00:00")
    LocalDateTime createdAt
) {}
