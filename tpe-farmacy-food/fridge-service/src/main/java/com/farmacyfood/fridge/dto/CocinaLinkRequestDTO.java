package com.farmacyfood.fridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para vincular una cocina a una heladera")
public record CocinaLinkRequestDTO(
    @Schema(description = "ID de la cocina a vincular", example = "1")
    @NotNull Long cocinaId
) {}
