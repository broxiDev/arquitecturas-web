package com.farmacyfood.order.controller;

import com.farmacyfood.order.dto.OrderCancelDTO;
import com.farmacyfood.order.dto.OrderCreateDTO;
import com.farmacyfood.order.dto.OrderResponseDTO;
import com.farmacyfood.order.dto.PaymentResponseDTO;
import com.farmacyfood.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@RequiredArgsConstructor
@Tag(name = "Order Service", description = "Órdenes y pagos")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Crear una nueva orden")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "Stock insuficiente")
    })
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderCreateDTO orderCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderCreateDto));
    }

    @PostMapping("/{id}/pagar")
    @Operation(summary = "Pagar una orden existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago procesado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "409", description = "La orden no está en estado PENDING"),
            @ApiResponse(responseCode = "502", description = "Error del gateway de pago")
    })
    public PaymentResponseDTO payOrder(
            @Parameter(description = "ID de la orden", example = "1")
            @PathVariable Long id) {
        return orderService.payOrder(id);
    }

    @PostMapping("/{id}/confirmar-retiro")
    @Operation(summary = "Confirmar retiro de productos de la heladera")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retiro confirmado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "409", description = "La orden no está en estado PAID")
    })
    public OrderResponseDTO confirmPickup(
            @Parameter(description = "ID de la orden", example = "1")
            @PathVariable Long id) {
        return orderService.confirmPickup(id);
    }

    @PreAuthorize("hasAuthority('cocina')")
    @GetMapping
    @Operation(summary = "Listar todas las órdenes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de órdenes")
    })
    public List<OrderResponseDTO> getAll() {
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una orden por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden encontrada"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public OrderResponseDTO getById(
            @Parameter(description = "ID de la orden", example = "1")
            @PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping("/usuario")
    @Operation(summary = "Obtener órdenes de un usuario específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Órdenes del usuario Juan")
    })
    public List<OrderResponseDTO> getByUser() {
        return orderService.getByUser();
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una orden")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden cancelada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "409", description = "La orden no está en estado PENDING o PAID")
    })
    public OrderResponseDTO cancelOrder(
            @Parameter(description = "ID de la orden", example = "1")
            @PathVariable Long id,
            @RequestBody(required = false) OrderCancelDTO cancelDto) {
        return orderService.cancelOrder(id, cancelDto);
    }
}
