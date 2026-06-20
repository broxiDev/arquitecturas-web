package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Ventas agregadas por producto de la cocina fantasma")
public record ProductoVentaDTO(
    @Schema(description = "ID del producto", example = "101")
    Long productId,

    @Schema(description = "Nombre del producto", example = "Brownie de Chocolate")
    String productName,

    @Schema(description = "Cantidad total vendida en el rango", example = "70")
    Integer totalVendido,

    @Schema(description = "Monto total acumulado", example = "525000.00")
    BigDecimal totalMonto
) {}
