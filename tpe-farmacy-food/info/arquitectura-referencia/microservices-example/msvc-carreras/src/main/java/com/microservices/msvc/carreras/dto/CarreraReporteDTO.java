package com.microservices.msvc.carreras.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CarreraReporteDTO {
    private final String carrera;
    private final Integer anio;
    private final Long inscriptos;
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
