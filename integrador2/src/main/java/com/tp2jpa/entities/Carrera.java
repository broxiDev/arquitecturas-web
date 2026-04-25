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
@Table(name = "carrera")
public class Carrera {

    @Id
    @Column(name = "id_carrera")
    private Long idCarrera;

    @Column(name = "carrera")
    private String nombreCarrera;

    @Column
    private Integer duracion;

    @OneToMany(mappedBy = "carrera")
    private List<EstudianteCarrera> inscripciones = new ArrayList<>();

    public Carrera(Long idCarrera, String nombreCarrera, Integer duracion) {
        this.idCarrera = idCarrera;
        this.nombreCarrera = nombreCarrera;
        this.duracion = duracion;
    }
}
