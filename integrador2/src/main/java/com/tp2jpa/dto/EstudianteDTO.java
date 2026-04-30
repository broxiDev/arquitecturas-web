package com.tp2jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * DTO para representar datos basicos de un estudiante.
 */
@Getter
@AllArgsConstructor
@ToString
public class EstudianteDTO {

    /** Nombre del estudiante. */
    private final String nombre;

    /** Apellido del estudiante. */
    private final String apellido;

    /** Libreta universitaria. */
    private final Long lu;

    /** Genero del estudiante. */
    private final String genero;

    /** Ciudad de residencia. */
    private final String ciudad;
}
