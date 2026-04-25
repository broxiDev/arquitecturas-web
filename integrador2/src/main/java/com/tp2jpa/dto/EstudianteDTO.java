package com.tp2jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class EstudianteDTO {

    private final String nombre;
    private final String apellido;
    private final Long lu;
    private final String genero;
    private final String ciudad;
}
