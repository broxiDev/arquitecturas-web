package com.microservices.msvc.carreras.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MatricularRequestDTO {
    private Integer inscripcion;
    private Integer graduacion;
    private Integer antiguedad;
}
