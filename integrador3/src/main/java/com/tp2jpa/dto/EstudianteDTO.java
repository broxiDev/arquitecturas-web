package com.tp2jpa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * DTO para representar datos basicos de un estudiante.
 */
@Schema(description = "Datos básicos de un estudiante para respuestas de la API")
@Getter
@AllArgsConstructor
@ToString
public class EstudianteDTO {

    @Schema(description = "Nombre del estudiante")
    private final String nombre;

    @Schema(description = "Apellido del estudiante")
    private final String apellido;

    @Schema(description = "Número de libreta universitaria")
    private final Long lu;

    @Schema(description = "Género del estudiante")
    private final String genero;

    @Schema(description = "Ciudad de residencia")
    private final String ciudad;
}
