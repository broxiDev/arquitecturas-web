package com.microservices.msvc.estudiantes.controllers;

import com.microservices.msvc.estudiantes.entities.Estudiante;
import com.microservices.msvc.estudiantes.services.EstudianteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@Tag(name = "Estudiantes", description = "CRUD de estudiantes")
public class EstudianteController {

    @Autowired
    private EstudianteService service;

    @Operation(summary = "c) Todos los estudiantes ordenados por apellido")
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(service.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al recuperar estudiantes.\"}");
        }
    }

    @Operation(summary = "Buscar estudiante por DNI (usado internamente por msvc-carreras via Feign)")
    @GetMapping("/{dni}")
    public ResponseEntity<?> getById(@PathVariable Long dni) {
        try {
            return service.findById(dni)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Estudiante no encontrado.\"}");
        }
    }

    @Operation(summary = "d) Buscar estudiante por numero de libreta universitaria")
    @GetMapping("/lu/{lu}")
    public ResponseEntity<?> getByLu(@PathVariable Long lu) {
        try {
            return service.findByLu(lu)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Estudiante no encontrado.\"}");
        }
    }

    @Operation(summary = "e) Estudiantes por genero ordenados por apellido")
    @GetMapping("/genero/{genero}")
    public ResponseEntity<?> getByGenero(@PathVariable String genero) {
        try {
            return ResponseEntity.ok(service.findByGenero(genero));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al recuperar estudiantes.\"}");
        }
    }

    @Operation(summary = "Buscar estudiantes por lista de DNIs (uso interno - llamado por msvc-carreras via Feign)")
    @GetMapping("/bulk")
    public ResponseEntity<?> getBulk(@RequestParam("dnis") List<Long> dnis) {
        try {
            return ResponseEntity.ok(service.findBulk(dnis));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al recuperar estudiantes.\"}");
        }
    }

    @Operation(summary = "a) Alta de estudiante")
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Estudiante entity) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(entity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"Error al crear estudiante.\"}");
        }
    }
}
