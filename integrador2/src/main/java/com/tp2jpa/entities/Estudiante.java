package com.tp2jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "inscripciones")
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

    @OneToMany(mappedBy = "estudiante")
    private List<EstudianteCarrera> inscripciones = new ArrayList<>();

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
