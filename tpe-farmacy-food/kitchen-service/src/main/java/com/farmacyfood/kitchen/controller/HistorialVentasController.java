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
@Tag(name = "Historial de Ventas", description = "Consulta de ventas históricas de la cocina fantasma")
public class HistorialVentasController {

    private final HistorialVentasService historialVentasService;

    @Operation(
        summary = "Obtener historial de ventas de la cocina fantasma",
        description = "Retorna el historial de ventas con filtros opcionales por producto, heladera y rango de fechas. Sin filtros devuelve todos los registros."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de ventas históricas",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = VentaHistoricaResponseDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Sin filtros",
                        summary = "Todos los registros",
                        value = """
                        [
                          {"productId": 101, "productName": "Brownie de Chocolate", "fridgeId": 1, "quantity": 10, "totalAmount": 75000.00, "date": "2026-06-19"},
                          {"productId": 101, "productName": "Brownie de Chocolate", "fridgeId": 2, "quantity": 8, "totalAmount": 60000.00, "date": "2026-06-18"},
                          {"productId": 102, "productName": "Cheesecake", "fridgeId": 1, "quantity": 7, "totalAmount": 66500.00, "date": "2026-06-19"},
                          {"productId": 102, "productName": "Cheesecake", "fridgeId": 2, "quantity": 5, "totalAmount": 47500.00, "date": "2026-06-17"},
                          {"productId": 103, "productName": "Tiramis\u00fa", "fridgeId": 1, "quantity": 12, "totalAmount": 105600.00, "date": "2026-06-18"},
                          {"productId": 103, "productName": "Tiramis\u00fa", "fridgeId": 2, "quantity": 9, "totalAmount": 79200.00, "date": "2026-06-20"},
                          {"productId": 201, "productName": "Tostada de Palta Sin Gluten", "fridgeId": 3, "quantity": 12, "totalAmount": 86400.00, "date": "2026-06-19"},
                          {"productId": 201, "productName": "Tostada de Palta Sin Gluten", "fridgeId": 4, "quantity": 9, "totalAmount": 64800.00, "date": "2026-06-18"},
                          {"productId": 301, "productName": "Buddha Bowl Vegano", "fridgeId": 5, "quantity": 12, "totalAmount": 102000.00, "date": "2026-06-19"},
                          {"productId": 301, "productName": "Buddha Bowl Vegano", "fridgeId": 6, "quantity": 8, "totalAmount": 68000.00, "date": "2026-06-18"}
                        ]
                        """
                    ),
                    @ExampleObject(
                        name = "Por producto",
                        summary = "productId=101",
                        value = """
                        [
                          {"productId": 101, "productName": "Brownie de Chocolate", "fridgeId": 1, "quantity": 10, "totalAmount": 75000.00, "date": "2026-06-19"},
                          {"productId": 101, "productName": "Brownie de Chocolate", "fridgeId": 2, "quantity": 8, "totalAmount": 60000.00, "date": "2026-06-18"}
                        ]
                        """
                    ),
                    @ExampleObject(
                        name = "Por heladera",
                        summary = "fridgeId=1",
                        value = """
                        [
                          {"productId": 101, "productName": "Brownie de Chocolate", "fridgeId": 1, "quantity": 10, "totalAmount": 75000.00, "date": "2026-06-19"},
                          {"productId": 102, "productName": "Cheesecake", "fridgeId": 1, "quantity": 7, "totalAmount": 66500.00, "date": "2026-06-19"},
                          {"productId": 103, "productName": "Tiramis\u00fa", "fridgeId": 1, "quantity": 12, "totalAmount": 105600.00, "date": "2026-06-18"}
                        ]
                        """
                    ),
                    @ExampleObject(
                        name = "Combinado",
                        summary = "productId=101 & fridgeId=1 & from=2026-06-01 & to=2026-06-14",
                        value = """
                        [
                          {"productId": 101, "productName": "Brownie de Chocolate", "fridgeId": 1, "quantity": 10, "totalAmount": 75000.00, "date": "2026-06-13"}
                        ]
                        """
                    ),
                    @ExampleObject(
                        name = "Combinado 2",
                        summary = "productId=103 & fridgeId=1 & from=2026-06-01 & to=2026-06-22",
                        value = """
                        [
                          {"productId": 103, "productName": "Tiramis\u00fa", "fridgeId": 1, "quantity": 12, "totalAmount": 105600.00, "date": "2026-06-18"}
                        ]
                        """
                    )
                }
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<VentaHistoricaResponseDTO>> getHistorial(
            @Parameter(description = "Fecha desde (YYYY-MM-DD)", example = "2026-06-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Fecha hasta (YYYY-MM-DD)", example = "2026-06-22")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "ID del producto", example = "103")
            @RequestParam(required = false) Long productId,
            @Parameter(description = "ID de la heladera", example = "1")
            @RequestParam(required = false) Long fridgeId) {

        List<VentaHistoricaResponseDTO> ventas = historialVentasService.getVentas(from, to, productId, fridgeId);
        return ResponseEntity.ok(ventas);
    }
}
