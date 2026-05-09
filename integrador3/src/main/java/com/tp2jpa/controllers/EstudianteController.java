package com.tp2jpa.controllers;

import com.tp2jpa.dto.MatricularRequestDTO;
import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.servicios.EstudianteServicio;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estudiantes")
public class EstudianteController {

    @Autowired
    private EstudianteServicio estudianteServicio;

    @Operation(summary = "Listar todos los estudiantes ordenados por apellido")
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(estudianteServicio.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Error. Por favor intente más tarde.\"}");
        }
    }

    @Operation(summary = "Recuperar un estudiante por número de libreta universitaria")
    @GetMapping("/lu/{lu}")
    public ResponseEntity<?> getByLU(@PathVariable Long lu) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(estudianteServicio.buscarPorLU(lu));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"No se encontró el estudiante.\"}");
        }
    }

    @Operation(summary = "Recuperar todos los estudiantes por género")
    @GetMapping("/genero/{genero}")
    public ResponseEntity<?> getByGenero(@PathVariable String genero) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(estudianteServicio.buscarPorGenero(genero));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Error. Por favor intente más tarde.\"}");
        }
    }

    @Operation(summary = "Recuperar estudiantes de una carrera filtrados por ciudad de residencia")
    @GetMapping("/carrera/{carrera}")
    public ResponseEntity<?> getByCarreraYCiudad(@PathVariable String carrera, @RequestParam String ciudad) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(estudianteServicio.buscarPorCarreraYCiudad(carrera, ciudad));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Error. Por favor intente más tarde.\"}");
        }
    }

    @Operation(summary = "Dar de alta un estudiante")
    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody Estudiante entity) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(estudianteServicio.save(entity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"Error. No se pudo crear el estudiante, revise los campos.\"}");
        }
    }

    @Operation(summary = "Matricular un estudiante en una carrera")
    @PostMapping("/{dni}/carreras/{idCarrera}")
    public ResponseEntity<?> matricular(
            @PathVariable Long dni,
            @PathVariable Long idCarrera,
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
