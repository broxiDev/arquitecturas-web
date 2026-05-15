package com.tp2jpa.entities;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una carrera universitaria.
 */
@Schema(description = "Entidad Carrera: información básica de una carrera universitaria")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "inscripciones")
@Entity
@Table(name = "carrera")
public class Carrera {

    @Schema(description = "Identificador único de la carrera")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrera")
    private Long idCarrera;

    @Schema(description = "Nombre de la carrera")
    @Column(name = "carrera")
    private String nombreCarrera;

    @Schema(description = "Duración estimada en años")
    @Column
    private Integer duracion;

    @Schema(description = "Lista de inscripciones de estudiantes a la carrera (OneToMany)")
    @JsonIgnore
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
