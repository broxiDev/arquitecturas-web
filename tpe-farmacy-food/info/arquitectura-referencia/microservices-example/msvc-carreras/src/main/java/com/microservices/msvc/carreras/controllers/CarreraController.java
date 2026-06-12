package com.microservices.msvc.carreras.controllers;

import com.microservices.msvc.carreras.dto.MatricularRequestDTO;
import com.microservices.msvc.carreras.services.CarreraService;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Carreras y Matriculacion")
public class CarreraController {

    @Autowired
    private CarreraService service;

    @Operation(summary = "f) Carreras con cantidad de inscriptos, ordenadas DESC")
    @GetMapping("/api/carreras")
    public ResponseEntity<?> getCarrerasConInscriptos() {
        try {
            return ResponseEntity.ok(service.getCarrerasConInscriptos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al recuperar carreras.\"}");
        }
    }

    @Operation(summary = "h) Reporte anual: inscriptos y egresados por carrera y anio")
    @GetMapping("/api/carreras/reporte")
    public ResponseEntity<?> getReporte() {
        try {
            return ResponseEntity.ok(service.getReporteAnual());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al generar reporte.\"}");
        }
    }

    @Operation(summary = "b) Matricular un estudiante en una carrera")
    @PostMapping("/api/estudiantes/{dni}/carreras/{idCarrera}")
    public ResponseEntity<?> matricular(
            @PathVariable Long dni,
            @PathVariable Long idCarrera,
            @RequestBody MatricularRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    service.matricular(dni, idCarrera,
                            request.getInscripcion(), request.getGraduacion(), request.getAntiguedad())
            );
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Estudiante no encontrado con DNI: " + dni + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"Error al matricular: " + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "g) Estudiantes de una carrera filtrados por ciudad de residencia")
    @GetMapping("/api/estudiantes/carrera/{carrera}")
    public ResponseEntity<?> getByCarreraYCiudad(
            @PathVariable String carrera,
            @RequestParam String ciudad) {
        try {
            return ResponseEntity.ok(service.buscarPorCarreraYCiudad(carrera, ciudad));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al buscar estudiantes.\"}");
        }
    }
}
