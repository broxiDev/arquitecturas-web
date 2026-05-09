package com.tp2jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * DTO para la consulta de carreras con cantidad de inscriptos.
 */
@Getter
@AllArgsConstructor
@ToString
public class CarreraInscriptosDTO {

    /** Nombre de la carrera. */
    private final String nombre;

    /** Cantidad de estudiantes inscriptos. */
    private final Long cantidadInscriptos;
}
