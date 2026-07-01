package com.farmacyfood.kitchen.controller;

import com.farmacyfood.kitchen.dto.CocinaCreateDTO;
import com.farmacyfood.kitchen.dto.CocinaResponseDTO;
import com.farmacyfood.kitchen.service.CocinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/cocina")
@RequiredArgsConstructor
@Tag(name = "Gestion de cocina por usuario", description = "Crear y buscar cocinas fantasma asociadas a usuarios")
public class CocinaController {

    private final CocinaService cocinaService;

    @Operation(
        summary = "Crear cocina fantasma",
        description = "Crea una cocina fantasma asociada al usuario autenticado (resuelto desde el header X-User). Valida que el usuario exista en user-service y que no tenga ya una cocina asignada."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Cocina creada exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CocinaResponseDTO.class),
                examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"La mejor cocina veggy de Rosi\",\"usuario\":\"auth_juan\",\"createdAt\":\"2026-06-27T14:00:00\"}")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Usuario no existe o ya tiene cocina",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"error\":\"Error en cocina\",\"message\":\"El usuario 11 no existe\",\"timestamp\":\"2026-06-27T14:00:00\"}")
            )
        )
    })
    @PostMapping
    public ResponseEntity<CocinaResponseDTO> crear(@Valid @RequestBody CocinaCreateDTO request) {
        log.info("Controller: creando cocina '{}'", request.nombre());
        CocinaResponseDTO response = cocinaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar cocina por ID")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cocina encontrada",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CocinaResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Cocina no encontrada",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"error\":\"Error en cocina\",\"message\":\"No existe cocina con id: 99\",\"timestamp\":\"2026-06-27T14:00:00\"}")
            )
        )
    })
    @GetMapping("/{cocinaId}")
    public ResponseEntity<CocinaResponseDTO> buscar(
            @Parameter(description = "ID de la cocina (cocinaId)", required = true, example = "1")
            @PathVariable Long cocinaId) {
        CocinaResponseDTO response = cocinaService.buscar(cocinaId);
        return ResponseEntity.ok(response);
    }
}
