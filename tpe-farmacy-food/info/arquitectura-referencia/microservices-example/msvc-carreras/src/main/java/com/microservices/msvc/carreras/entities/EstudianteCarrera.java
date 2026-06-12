package com.microservices.msvc.carreras.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "carrera")
@Entity
@Table(name = "estudiante_carrera")
public class EstudianteCarrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK al estudiante en msvc-estudiantes — no se mapea como @ManyToOne
    @Column(name = "id_estudiante")
    private Long dniEstudiante;

    @ManyToOne
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    @Column
    private Integer inscripcion;

    @Column
    private Integer graduacion;

    @Column
    private Integer antiguedad;

    public EstudianteCarrera(Long dniEstudiante, Carrera carrera,
                              Integer inscripcion, Integer graduacion, Integer antiguedad) {
        this.dniEstudiante = dniEstudiante;
        this.carrera = carrera;
        this.inscripcion = inscripcion;
        this.graduacion = graduacion;
        this.antiguedad = antiguedad;
    }
}
