package com.tp2jpa.entities;

import jakarta.persistence.*;
    import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad intermedia que modela la relacion entre estudiante y carrera.
 * Contiene datos de inscripcion y graduacion.
 */
    @Schema(description = "Entidad que representa la inscripción de un estudiante en una carrera (años, antigüedad, graduación)")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"estudiante", "carrera"})
@Entity
@Table(name = "estudiante_carrera")
public class EstudianteCarrera {

    /** Identificador unico de la inscripcion. */
        @Schema(description = "Identificador único de la inscripción")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Estudiante asociado a la inscripcion. */
        @Schema(description = "Estudiante asociado a la inscripción")
    @ManyToOne
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;

    /** Carrera asociada a la inscripcion. */
        @Schema(description = "Carrera asociada a la inscripción")
    @ManyToOne
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    /** Año de inscripcion a la carrera. */
        @Schema(description = "Año de inscripción en la carrera")
    @Column
    private Integer inscripcion;

    /** Año de graduacion en la carrera. */
        @Schema(description = "Año de graduación en la carrera (0 si no egresó aún)")
    @Column
    private Integer graduacion;

    /** Antiguedad academica del estudiante en la carrera. */
        @Schema(description = "Antigüedad académica del estudiante en la carrera (años)")
    @Column
    private Integer antiguedad;

    /**
     * Constructor para crear una inscripcion con sus datos principales.
     *
     * @param carrera carrera asociada
     * @param inscripcion año de inscripcion
     * @param graduacion año de graduacion
     * @param antiguedad antiguedad en la carrera
     */
    public EstudianteCarrera(Carrera carrera, Integer inscripcion, Integer graduacion, Integer antiguedad) {
        this.carrera = carrera;
        this.inscripcion = inscripcion;
        this.graduacion = graduacion;
        this.antiguedad = antiguedad;
    }
}
