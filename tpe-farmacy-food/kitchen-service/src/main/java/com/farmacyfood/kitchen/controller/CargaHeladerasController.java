package com.farmacyfood.kitchen.controller;

import com.farmacyfood.kitchen.dto.CargaHeladeraRequestDTO;
import com.farmacyfood.kitchen.service.CargaHeladerasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/cocina/carga-heladeras")
@RequiredArgsConstructor
@Tag(name = "Agregar productos por heladera", description = "Cargar productos de una cocina en heladeras")
public class CargaHeladerasController {

    private final CargaHeladerasService cargaHeladerasService;

    @Operation(
        summary = "Cargar productos de una cocina en una heladera",
        description = "Valida que los productos existan en el cocina local de la cocina y los envia a fridge-service para cargarlos en la heladera especificada."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Carga exitosa",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"mensaje\":\"Carga exitosa de 2 productos en heladera 1\"}")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Producto no existe en el cocina de la cocina",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"error\":\"Error en carga de heladeras\",\"message\":\"El producto 101 no existe en el cocina de la cocina COCINA-VEGETARIANA\",\"timestamp\":\"2026-06-14T12:00:00\"}")
            )
        )
    })
    @PostMapping
    public ResponseEntity<String> cargar(@Valid @RequestBody CargaHeladeraRequestDTO request) {
        log.info("Controller: carga de {} productos del cocina {} en heladera {}",
                request.productos().size(), request.cocinaId(), request.heladeraId());
        cargaHeladerasService.cargar(request);
        return ResponseEntity.ok(
                "Carga exitosa de " + request.productos().size()
                        + " productos en heladera " + request.heladeraId());
    }
}
