package com.farmacyfood.kitchen.dto;

import java.math.BigDecimal;

public record ProductoVentaDTO(
    Long productId,
    String productName,
    Integer totalVendido,
    BigDecimal totalMonto
) {}
