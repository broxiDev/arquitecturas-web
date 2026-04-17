package com.tp2jpa.dto;

public class CarreraReporteDTO {

    private String carrera;
    private Integer anio;
    private Long inscriptos;
    private Long egresados;

    public CarreraReporteDTO(String carrera, Integer anio, Long inscriptos, Long egresados) {
        this.carrera = carrera;
        this.anio = anio;
        this.inscriptos = inscriptos;
        this.egresados = egresados;
    }

    public String getCarrera() { return carrera; }
    public Integer getAnio() { return anio; }
    public Long getInscriptos() { return inscriptos; }
    public Long getEgresados() { return egresados; }

    @Override
    public String toString() {
        return "Carrera: " + carrera
                + " | Año: " + anio
                + " | Inscriptos: " + inscriptos
                + " | Egresados: " + egresados;
    }
}

