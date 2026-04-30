package com.tp2jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * DTO para filas del reporte anual por carrera.
 */
@Getter
@AllArgsConstructor
@ToString
public class CarreraReporteDTO {

    /** Nombre de la carrera. */
    private final String carrera;

    /** Año del reporte. */
    private final Integer anio;

    /** Cantidad de inscriptos en ese año. */
    private final Long inscriptos;

    /** Cantidad de egresados en ese año. */
    private final Long egresados;
}
