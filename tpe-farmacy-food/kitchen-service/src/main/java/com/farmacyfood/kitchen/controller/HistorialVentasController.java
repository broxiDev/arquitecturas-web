package com.farmacyfood.kitchen.controller;

import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import com.farmacyfood.kitchen.service.HistorialVentasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cocina/historial-ventas")
@RequiredArgsConstructor
@Tag(name = "Historial de Ventas", description = "Consulta de ventas históricas con filtros")
public class HistorialVentasController {

    private final HistorialVentasService historialVentasService;

    @Operation(
        summary = "Obtener historial de ventas",
        description = "Retorna el historial de ventas con filtros opcionales. Sin filtros devuelve todos los registros."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de ventas históricas",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = VentaHistoricaResponseDTO.class),
                examples = @ExampleObject(
                    name = "Historial de ventas",
                    value = """
                    [
                      {
                        "productId": 101,
                        "productName": "Brownie de Chocolate",
                        "fridgeId": 1,
                        "quantity": 15,
                        "totalAmount": 11250.00,
                        "date": "2026-06-13"
                      },
                      {
                        "productId": 101,
                        "productName": "Brownie de Chocolate",
                        "fridgeId": 2,
                        "quantity": 10,
                        "totalAmount": 7500.00,
                        "date": "2026-06-12"
                      },
                      {
                        "productId": 102,
                        "productName": "Cheesecake",
                        "fridgeId": 1,
                        "quantity": 8,
                        "totalAmount": 7600.00,
                        "date": "2026-06-13"
                      }
                    ]
                    """
                )
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<VentaHistoricaResponseDTO>> getHistorial(
            @Parameter(description = "Fecha desde (YYYY-MM-DD)", example = "2026-06-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Fecha hasta (YYYY-MM-DD)", example = "2026-06-14")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "ID del producto", example = "101")
            @RequestParam(required = false) Long productId,
            @Parameter(description = "ID de la heladera", example = "1")
            @RequestParam(required = false) Long fridgeId) {

        List<VentaHistoricaResponseDTO> ventas = historialVentasService.getVentas(from, to, productId, fridgeId);
        return ResponseEntity.ok(ventas);
    }
}
