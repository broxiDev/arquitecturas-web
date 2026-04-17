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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nombres;

    @Column
    private String apellido;

    @Column
    private Integer edad;

    @Column(length = 1)
    private Character genero;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Column(name = "ciudad_residencia")
    private String ciudadResidencia;

    @Column(name = "numero_libreta_universitaria")
    private String numeroLibretaUniversitaria;

    @OneToMany(mappedBy = "estudiante")
    private List<Inscripcion> inscripciones = new ArrayList<>();

    public Estudiante(String nombres, String apellido, Integer edad, Character genero,
                      String numeroDocumento, String ciudadResidencia, String numeroLibretaUniversitaria) {
        this.nombres = nombres;
        this.apellido = apellido;
        this.edad = edad;
        this.genero = genero;
        this.numeroDocumento = numeroDocumento;
        this.ciudadResidencia = ciudadResidencia;
        this.numeroLibretaUniversitaria = numeroLibretaUniversitaria;
    }
}
