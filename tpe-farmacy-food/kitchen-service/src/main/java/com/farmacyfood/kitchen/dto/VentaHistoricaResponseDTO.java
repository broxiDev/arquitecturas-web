package com.farmacyfood.kitchen.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VentaHistoricaResponseDTO(
    Long productId,
    String productName,
    Long fridgeId,
    Integer quantity,
    BigDecimal totalAmount,
    LocalDate date
) {}
