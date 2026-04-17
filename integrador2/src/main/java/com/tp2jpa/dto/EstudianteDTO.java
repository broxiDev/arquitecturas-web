package com.tp2jpa.dto;

import com.tp2jpa.entities.Estudiante;

public class EstudianteDTO {

    private String nombres;
    private String apellido;
    private String numeroLibretaUniversitaria;
    private Estudiante.Genero genero;
    private String ciudadResidencia;

    public EstudianteDTO(String nombres, String apellido, String numeroLibretaUniversitaria,
                         Estudiante.Genero genero, String ciudadResidencia) {
        this.nombres = nombres;
        this.apellido = apellido;
        this.numeroLibretaUniversitaria = numeroLibretaUniversitaria;
        this.genero = genero;
        this.ciudadResidencia = ciudadResidencia;
    }

    public String getNombres() { return nombres; }
    public String getApellido() { return apellido; }
    public String getNumeroLibretaUniversitaria() { return numeroLibretaUniversitaria; }
    public Estudiante.Genero getGenero() { return genero; }
    public String getCiudadResidencia() { return ciudadResidencia; }

    @Override
    public String toString() {
        return apellido + ", " + nombres
                + " | LU: " + numeroLibretaUniversitaria
                + " | Género: " + genero
                + " | Ciudad: " + ciudadResidencia;
    }
}
