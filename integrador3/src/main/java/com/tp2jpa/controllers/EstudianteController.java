package com.tp2jpa.controllers;

import com.tp2jpa.dto.MatricularRequestDTO;
import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.servicios.EstudianteServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estudiantes")
@Tag(name = "Estudiantes", description = "Endpoints para gestionar estudiantes: alta, consultas y matrícula")
public class EstudianteController {

    @Autowired
    private EstudianteServicio estudianteServicio;

    @Operation(summary = "c) Recuperar todos los estudiantes ordenados por apellido")
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(estudianteServicio.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Error. Por favor intente más tarde.\"}");
        }
    }

    @Operation(summary = "d) Recuperar un estudiante por número de libreta universitaria")
    @GetMapping("/lu/{lu}")
    public ResponseEntity<?> getByLU(@Parameter(example = "1001") @PathVariable Long lu) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(estudianteServicio.buscarPorLU(lu));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"No se encontró el estudiante.\"}");
        }
    }

    @Operation(summary = "e) Recuperar todos los estudiantes por género")
    @GetMapping("/genero/{genero}")
    public ResponseEntity<?> getByGenero(@Parameter(example = "Male") @PathVariable String genero) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(estudianteServicio.buscarPorGenero(genero));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Error. Por favor intente más tarde.\"}");
        }
    }

    @Operation(summary = "g) Recuperar estudiantes de una carrera filtrados por ciudad")
    @GetMapping("/carrera/{carrera}")
    public ResponseEntity<?> getByCarreraYCiudad(
            @Parameter(example = "TUDAI") @PathVariable String carrera,
            @Parameter(example = "Paquera") @RequestParam String ciudad) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(estudianteServicio.buscarPorCarreraYCiudad(carrera, ciudad));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Error. Por favor intente más tarde.\"}");
        }
    }

    @Operation(summary = "a) Dar de alta un estudiante")
    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody Estudiante entity) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(estudianteServicio.save(entity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"Error. No se pudo crear el estudiante, revise los campos.\"}");
        }
    }

    @Operation(summary = "b) Matricular un estudiante en una carrera")
    @PostMapping("/{dni}/carreras/{idCarrera}")
    public ResponseEntity<?> matricular(
            @Parameter(example = "12345670") @PathVariable Long dni,
            @Parameter(example = "1") @PathVariable Long idCarrera,
            @RequestBody MatricularRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    estudianteServicio.matricular(dni, idCarrera,
                            request.getInscripcion(), request.getGraduacion(), request.getAntiguedad())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"Error. No se pudo matricular al estudiante.\"}");
        }
    }
}
