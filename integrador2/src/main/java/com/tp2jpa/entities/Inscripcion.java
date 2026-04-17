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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrera_id", nullable = false)
    private Carrera carrera;

    @Column(nullable = false)
    private Integer antiguedadEnAnios;

    @Column(nullable = false)
    private Boolean graduado;

    public Inscripcion(Carrera carrera, Integer antiguedadEnAnios, Boolean graduado) {
        this.carrera = carrera;
        this.antiguedadEnAnios = antiguedadEnAnios;
        this.graduado = graduado;
    }
}
