package com.tp2jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CarreraInscriptosDTO {

    private final String nombre;
    private final Long cantidadInscriptos;
}
