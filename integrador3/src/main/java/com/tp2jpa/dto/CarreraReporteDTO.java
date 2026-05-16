package com.tp2jpa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

/**
 * DTO para filas del reporte anual por carrera.
 */
@Schema(description = "Fila del reporte anual por carrera: año, inscriptos y egresados")
@Getter
@ToString
public class CarreraReporteDTO {

    @Schema(description = "Nombre de la carrera")
    private final String carrera;

    @Schema(description = "Año del reporte")
    private final Integer anio;

    @Schema(description = "Cantidad de inscriptos en ese año")
    private final Long inscriptos;

    @Schema(description = "Cantidad de egresados en ese año")
    private final Long egresados;

    public CarreraReporteDTO(String carrera, Integer anio, Long inscriptos, Long egresados) {
        this.carrera = carrera;
        this.anio = anio;
        this.inscriptos = inscriptos;
        this.egresados = egresados;
    }

    // Hibernate puede resolver 0L como Integer en SELECT new
    public CarreraReporteDTO(String carrera, Integer anio, Long inscriptos, Integer egresados) {
        this(carrera, anio, inscriptos, egresados.longValue());
    }

    public CarreraReporteDTO(String carrera, Integer anio, Integer inscriptos, Long egresados) {
        this(carrera, anio, inscriptos.longValue(), egresados);
    }
}
