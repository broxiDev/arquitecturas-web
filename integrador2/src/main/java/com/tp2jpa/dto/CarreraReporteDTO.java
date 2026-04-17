package com.tp2jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CarreraReporteDTO {

    private final String carrera;
    private final Integer anio;
    private final Long inscriptos;
    private final Long egresados;
}
