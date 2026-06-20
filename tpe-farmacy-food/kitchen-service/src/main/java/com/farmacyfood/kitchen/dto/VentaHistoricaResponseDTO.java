package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Registro de venta histórica")
public record VentaHistoricaResponseDTO(
    @Schema(description = "ID del producto", example = "101")
    Long productId,

    @Schema(description = "Nombre del producto", example = "Brownie de Chocolate")
    String productName,

    @Schema(description = "ID de la heladera", example = "1")
    Long fridgeId,

    @Schema(description = "Cantidad vendida", example = "12")
    Integer quantity,

    @Schema(description = "Monto total de la venta", example = "6000.00")
    BigDecimal totalAmount,

    @Schema(description = "Fecha de la venta", example = "2026-06-13")
    LocalDate date
) {}
