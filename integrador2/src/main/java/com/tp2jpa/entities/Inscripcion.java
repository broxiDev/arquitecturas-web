package com.tp2jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"estudiante", "carrera"})
@Entity
@Table(name = "inscripcion")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;

    @Column(name = "antiguedad_en_anios")
    private Integer antiguedadEnAnios;

    @Column
    private Boolean graduado;

    public Inscripcion(Carrera carrera, Integer antiguedadEnAnios, Boolean graduado) {
        this.carrera = carrera;
        this.antiguedadEnAnios = antiguedadEnAnios;
        this.graduado = graduado;
    }
}
