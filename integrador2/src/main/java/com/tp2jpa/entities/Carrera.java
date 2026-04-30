package com.tp2jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una carrera universitaria.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "inscripciones")
@Entity
@Table(name = "carrera")
public class Carrera {

    /** Identificador unico de la carrera. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrera")
    private Long idCarrera;

    /** Nombre de la carrera. */
    @Column(name = "carrera")
    private String nombreCarrera;

    /** Duracion estimada en años. */
    @Column
    private Integer duracion;

    /** Lista de inscripciones de estudiantes a la carrera. */
    @OneToMany(mappedBy = "carrera")
    private List<EstudianteCarrera> inscripciones = new ArrayList<>();

    /**
     * Constructor con campos principales.
     *
     * @param idCarrera identificador de la carrera
     * @param nombreCarrera nombre de la carrera
     * @param duracion duracion estimada en años
     */
    public Carrera(Long idCarrera, String nombreCarrera, Integer duracion) {
        this.idCarrera = idCarrera;
        this.nombreCarrera = nombreCarrera;
        this.duracion = duracion;
    }
}
