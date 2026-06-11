package com.microservices.msvc.carreras.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO espejo de msvc-estudiantes — usado para deserializar respuestas Feign
@Getter
@Setter
@NoArgsConstructor
public class Estudiante {
    private Long dni;
    private String nombre;
    private String apellido;
    private Integer edad;
    private String genero;
    private String ciudad;
    private Long lu;
}
