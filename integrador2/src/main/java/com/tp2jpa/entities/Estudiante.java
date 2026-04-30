package com.tp2jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un estudiante.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "inscripciones")
@Entity
@Table(name = "estudiante")
public class Estudiante {

    /** Documento nacional de identidad (clave primaria). */
    @Id
    @Column(name = "dni")
    private Long dni;

    /** Nombre del estudiante. */
    @Column
    private String nombre;

    /** Apellido del estudiante. */
    @Column
    private String apellido;

    /** Edad del estudiante. */
    @Column
    private Integer edad;

    /** Genero del estudiante. */
    @Column
    private String genero;

    /** Ciudad de residencia del estudiante. */
    @Column
    private String ciudad;

    /** Libreta universitaria del estudiante. */
    @Column
    private Long lu;

    /** Historial de inscripciones a carreras. */
    @OneToMany(mappedBy = "estudiante")
    private List<EstudianteCarrera> inscripciones = new ArrayList<>();

    /**
     * Constructor con los datos principales del estudiante.
     *
     * @param dni documento del estudiante
     * @param nombre nombre
     * @param apellido apellido
     * @param edad edad
     * @param genero genero
     * @param ciudad ciudad de residencia
     * @param lu libreta universitaria
     */
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
