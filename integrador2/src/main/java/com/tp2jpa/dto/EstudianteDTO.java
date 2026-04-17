package com.tp2jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class EstudianteDTO {

    private final String nombres;
    private final String apellido;
    private final String numeroLibretaUniversitaria;
    private final Character genero;
    private final String ciudadResidencia;
}
