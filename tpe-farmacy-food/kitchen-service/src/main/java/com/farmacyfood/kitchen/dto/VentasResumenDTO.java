package com.farmacyfood.kitchen.dto;

import java.time.LocalDate;
import java.util.List;

public record VentasResumenDTO(
    List<ProductoVentaDTO> productos,
    LocalDate from,
    LocalDate to
) {}
