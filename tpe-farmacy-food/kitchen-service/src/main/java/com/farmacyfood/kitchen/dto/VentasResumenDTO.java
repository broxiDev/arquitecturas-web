package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Resumen de ventas de la cocina fantasma en un rango de fechas")
public record VentasResumenDTO(
    @Schema(description = "Lista de productos con ventas agregadas")
    List<ProductoVentaDTO> productos,

    @Schema(description = "Fecha desde del rango", example = "2026-06-11")
    LocalDate from,

    @Schema(description = "Fecha hasta del rango", example = "2026-06-17")
    LocalDate to
) {}
