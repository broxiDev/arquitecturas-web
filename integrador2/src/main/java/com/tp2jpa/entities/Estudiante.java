package com.tp2jpa.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estudiante")
public class Estudiante {
    
    public enum Genero {
        FEMENINO,
        MASCULINO,
        OTRO,
        PREFIERO_NO_DECIRLO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombres;

    @Column(nullable = false, length = 80)
    private String apellido;

    @Column(nullable = false)
    private Integer edad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Genero genero;

    @Column(nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    @Column(nullable = false, length = 100)
    private String ciudadResidencia;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroLibretaUniversitaria;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones = new ArrayList<>();

    public Estudiante() {
    }

    public Estudiante(String nombres, String apellido, Integer edad, Genero genero, String numeroDocumento,
                      String ciudadResidencia, String numeroLibretaUniversitaria) {
        this.nombres = nombres;
        this.apellido = apellido;
        this.edad = edad;
        this.genero = genero;
        this.numeroDocumento = numeroDocumento;
        this.ciudadResidencia = ciudadResidencia;
        this.numeroLibretaUniversitaria = numeroLibretaUniversitaria;
    }

    public void agregarInscripcion(Inscripcion inscripcion) {
        inscripciones.add(inscripcion);
        inscripcion.setEstudiante(this);
    }

    public Long getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellido() {
        return apellido;
    }

    public Integer getEdad() {
        return edad;
    }

    public Genero getGenero() {
        return genero;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public String getCiudadResidencia() {
        return ciudadResidencia;
    }

    public String getNumeroLibretaUniversitaria() {
        return numeroLibretaUniversitaria;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public void setCiudadResidencia(String ciudadResidencia) {
        this.ciudadResidencia = ciudadResidencia;
    }
}
