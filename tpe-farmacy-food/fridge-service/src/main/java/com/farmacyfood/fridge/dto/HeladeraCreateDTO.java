package com.farmacyfood.fridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para crear una heladera")
public record HeladeraCreateDTO(
    @Schema(description = "Nombre de la heladera", example = "Heladera Palermo")
    @NotBlank String name,

    @Schema(description = "Latitud", example = "-34.6037")
    @NotNull Double latitude,

    @Schema(description = "Longitud", example = "-58.3816")
    @NotNull Double longitude,

    @Schema(description = "Dirección", example = "Av. Santa Fe 1234, Palermo")
    @NotBlank String address,

    @Schema(description = "Estado inicial", example = "ACTIVE")
    @NotBlank String status
) {}
