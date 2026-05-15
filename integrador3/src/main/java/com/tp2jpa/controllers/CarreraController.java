package com.tp2jpa.controllers;

import com.tp2jpa.servicios.CarreraServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carreras")
@Tag(name = "Carreras", description = "Operaciones sobre carreras: listados y reportes")
public class CarreraController {

    @Autowired
    private CarreraServicio carreraServicio;

    @Operation(summary = "Recuperar carreras con estudiantes inscriptos ordenadas por cantidad de inscriptos")
    @GetMapping("")
    public ResponseEntity<?> getCarrerasConInscriptos() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(carreraServicio.getCarrerasConInscriptos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Error. Por favor intente más tarde.\"}");
        }
    }

    @Operation(summary = "Generar reporte de carreras con inscriptos y egresados por año")
    @GetMapping("/reporte")
    public ResponseEntity<?> getReporte() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(carreraServicio.getReporteAnual());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Error. Por favor intente más tarde.\"}");
        }
    }
}
