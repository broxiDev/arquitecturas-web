package com.farmacyfood.kitchen.controller;

import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import com.farmacyfood.kitchen.service.HistorialVentasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cocina/historial-ventas")
@RequiredArgsConstructor
@Tag(name = "Historial de Ventas", description = "Consulta de ventas históricas")
public class HistorialVentasController {

    private final HistorialVentasService historialVentasService;

    @Operation(summary = "Obtener historial de ventas con filtros opcionales")
    @GetMapping
    public ResponseEntity<List<VentaHistoricaResponseDTO>> getHistorial(
            @Parameter(description = "Fecha desde (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Fecha hasta (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "ID del producto")
            @RequestParam(required = false) Long productId,
            @Parameter(description = "ID de la heladera")
            @RequestParam(required = false) Long fridgeId) {

        List<VentaHistoricaResponseDTO> ventas = historialVentasService.getVentas(from, to, productId, fridgeId);
        return ResponseEntity.ok(ventas);
    }
}
