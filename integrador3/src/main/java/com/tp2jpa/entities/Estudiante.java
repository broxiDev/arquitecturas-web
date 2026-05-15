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
 * Entidad que representa a un estudiante.
 */
@Schema(description = "Entidad Estudiante: contiene datos personales y referencias a inscripciones")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "inscripciones")
@Entity
@Table(name = "estudiante")
public class Estudiante {

    @Schema(description = "Documento nacional de identidad (clave primaria)")
    @Id
    @Column(name = "dni")
    private Long dni;

    @Schema(description = "Nombre del estudiante")
    @Column
    private String nombre;

    @Schema(description = "Apellido del estudiante")
    @Column
    private String apellido;

    @Schema(description = "Edad del estudiante")
    @Column
    private Integer edad;

    @Schema(description = "Género del estudiante")
    @Column
    private String genero;

    @Schema(description = "Ciudad de residencia del estudiante")
    @Column
    private String ciudad;

    @Schema(description = "Número de libreta universitaria")
    @Column
    private Long lu;

    @Schema(description = "Historial de inscripciones a carreras (relación OneToMany)")
    @JsonIgnore
    @OneToMany(mappedBy = "estudiante")
    private List<EstudianteCarrera> inscripciones = new ArrayList<>();

    /**
     * Constructor con los datos principales del estudiante.
     *
     * @param dni documento del estudiante
     * @param nombre nombre
     * @param apellido apellido
     * @param edad edad
     * @param genero genero
     * @param ciudad ciudad de residencia
     * @param lu libreta universitaria
     */
    public Estudiante(Long dni, String nombre, String apellido, Integer edad,
                      String genero, String ciudad, Long lu) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.genero = genero;
        this.ciudad = ciudad;
        this.lu = lu;
    }
}
