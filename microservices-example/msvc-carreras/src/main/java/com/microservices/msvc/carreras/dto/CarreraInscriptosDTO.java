package com.microservices.msvc.carreras.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CarreraInscriptosDTO {
    private final Long idCarrera;
    private final String nombre;
    private final Long cantidadInscriptos;
}
