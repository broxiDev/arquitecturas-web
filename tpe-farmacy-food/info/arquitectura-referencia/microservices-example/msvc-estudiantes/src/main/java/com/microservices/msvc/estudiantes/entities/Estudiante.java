package com.microservices.msvc.estudiantes.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "estudiante")
public class Estudiante {

    @Id
    @Column(name = "dni")
    private Long dni;

    @Column
    private String nombre;

    @Column
    private String apellido;

    @Column
    private Integer edad;

    @Column
    private String genero;

    @Column
    private String ciudad;

    @Column
    private Long lu;

    public Estudiante(Long dni, String nombre, String apellido, Integer edad,
                      String genero, String ciudad, Long lu) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.genero = genero;
        this.ciudad = ciudad;
        this.lu = lu;
    }
}
