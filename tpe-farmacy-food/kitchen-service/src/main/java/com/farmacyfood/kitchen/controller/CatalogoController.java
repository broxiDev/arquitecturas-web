package com.farmacyfood.kitchen.controller;

import com.farmacyfood.kitchen.dto.ProductoRequest;
import com.farmacyfood.kitchen.dto.ProductoResponse;
import com.farmacyfood.kitchen.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/cocina/catalogo")
@RequiredArgsConstructor
@Tag(name = "Catálogo de Cocina", description = "Gestión del catálogo de productos de la cocina fantasma")
public class CatalogoController {

    private final CatalogoService catalogoService;

    @Operation(
        summary = "Registrar producto en el catálogo de una cocina",
        description = "Registra un nuevo producto en el catálogo de una cocina fantasma. Si ya existe un producto con el mismo nombre en el catálogo de esa cocina, se actualiza (upsert).",
        responses = {
            @ApiResponse(responseCode = "201", description = "Producto registrado exitosamente en el catálogo"),
            @ApiResponse(responseCode = "400", description = "Datos de producto inválidos", content = @Content)
        }
    )
    @PostMapping("/{cocinaId}")
    public ResponseEntity<ProductoResponse> registrarProducto(
            @Parameter(description = "ID de la cocina fantasma", required = true, example = "COCINA-DULCE")
            @PathVariable String cocinaId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del producto a registrar", required = true,
                content = @Content(schema = @Schema(implementation = ProductoRequest.class)))
            @RequestBody ProductoRequest productoRequest) {
        log.info("Controller: registrando producto '{}' en catálogo de cocina '{}'", productoRequest.name(), cocinaId);
        ProductoResponse response = catalogoService.registrarProducto(cocinaId, productoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
