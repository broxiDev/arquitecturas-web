package com.tp2jpa.dto;

public class CarreraInscriptosDTO {

    private String nombre;
    private Long cantidadInscriptos;

    public CarreraInscriptosDTO(String nombre, Long cantidadInscriptos) {
        this.nombre = nombre;
        this.cantidadInscriptos = cantidadInscriptos;
    }

    public String getNombre() { return nombre; }
    public Long getCantidadInscriptos() { return cantidadInscriptos; }

    @Override
    public String toString() {
        return nombre + " → " + cantidadInscriptos + " inscripto(s)";
    }
}

