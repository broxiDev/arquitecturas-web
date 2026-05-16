package com.tp2jpa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * DTO para la consulta de carreras con cantidad de inscriptos.
 */
@Schema(description = "Resumen de una carrera con la cantidad de estudiantes inscriptos")
@Getter
@AllArgsConstructor
@ToString
public class CarreraInscriptosDTO {

    @Schema(description = "Identificador de la carrera")
    private final Long idCarrera;

    @Schema(description = "Nombre de la carrera")
    private final String nombre;

    @Schema(description = "Cantidad de estudiantes inscriptos en la carrera")
    private final Long cantidadInscriptos;
}
