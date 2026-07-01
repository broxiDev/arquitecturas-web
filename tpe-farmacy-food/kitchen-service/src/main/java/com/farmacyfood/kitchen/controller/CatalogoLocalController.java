package com.farmacyfood.kitchen.controller;

import com.farmacyfood.kitchen.dto.CatalogoLocalRequestDTO;
import com.farmacyfood.kitchen.dto.CatalogoLocalResponseDTO;
import com.farmacyfood.kitchen.service.CatalogoLocalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/cocina")
@RequiredArgsConstructor
@Tag(name = "Gestion de productos por cocina", description = "Agregar y listar productos del catalogo de una cocina")
public class CatalogoLocalController {

    private final CatalogoLocalService catalogoLocalService;

    @Operation(summary = "Agregar producto a la cocina del usuario autenticado")
    @PostMapping("/productos")
    public ResponseEntity<CatalogoLocalResponseDTO> registrarProducto(@Valid @RequestBody CatalogoLocalRequestDTO request) {
        log.info("Controller: registrando producto '{}' en catalogo local", request.productId());
        CatalogoLocalResponseDTO response = catalogoLocalService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar productos de la cocina del usuario autenticado")
    @GetMapping("/productos")
    public ResponseEntity<List<CatalogoLocalResponseDTO>> listarDeMiCocina() {
        List<CatalogoLocalResponseDTO> productos = catalogoLocalService.listarDeMiCocina();
        return ResponseEntity.ok(productos);
    }
}
