package com.farmacyfood.order.controller;


import com.farmacyfood.order.dto.HistoricalSaleDTO;
import com.farmacyfood.order.dto.ProductSaleDTO;
import com.farmacyfood.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@RequiredArgsConstructor
@Tag(name = "Order Service", description = "Órdenes y pagos")
public class SaleHistoryController {

    private final OrderService orderService;

    //Kitchen-service lo consume para obtener todas las ventas (órdenes PAID o PICKED_UP) en un rango de fechas,
    //con filtros opcionales por producto y heladera. Usa fechas como LocalDate (no LocalDateTime) porque el historial
    // se consulta por día calendario.
    @GetMapping("/historial-ventas")
    @Operation(summary = "Obtener historial de ventas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de ventas en el rango de fechas")
    })
    public List<HistoricalSaleDTO> getHistorialVentas(
            @Parameter(description = "Fecha desde (yyyy-MM-dd)", example = "2026-06-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Fecha hasta (yyyy-MM-dd)", example = "2026-06-20")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "Filtrar por ID de producto", example = "101")
            @RequestParam(required = false) Long productId,
            @Parameter(description = "Filtrar por ID de heladera", example = "1")
            @RequestParam(required = false) Long fridgeId) {
        return orderService.getHistorialVentas(from, to, productId, fridgeId);
    }

    //Busca órdenes completadas entre from y to de tal cocina usando el repository. Por cada orden, itera sus OrderItem
    // y construye un HistoricalSaleDTO por item (desnormalizando producto, heladera, cantidad, total calculado
    // y fecha). Si productId está presente, filtra solo ese producto. Devuelve una lista plana de ventas individuales.
    @GetMapping("/historial-ventas/cocina/{cocinaId}")
    @Operation(summary = "Obtener ventas agregadas por producto para una cocina")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ventas agregadas por producto")
    })
    public List<ProductSaleDTO> getSalesByKitchen(
            @Parameter(description = "ID de la cocina", example = "cocina-1")
            @PathVariable String cocinaId,
            @Parameter(description = "Fecha desde (yyyy-MM-dd)", example = "2026-06-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Fecha hasta (yyyy-MM-dd)", example = "2026-06-20")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return orderService.getSalesByKitchen(cocinaId, from, to);
    }
}
