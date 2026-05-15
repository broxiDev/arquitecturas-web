package com.tp2jpa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Request para matricular un estudiante en una carrera: años de inscripción, graduación y antigüedad")
@Getter
@Setter
@NoArgsConstructor
public class MatricularRequestDTO {

    @Schema(description = "Año de inscripción en la carrera (ej: 2023)")
    private Integer inscripcion;

    @Schema(description = "Año de graduación en la carrera (0 si no egresó aún)")
    private Integer graduacion;

    @Schema(description = "Antigüedad académica en años dentro de la carrera")
    private Integer antiguedad;
}
