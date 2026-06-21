package com.farmacyfood.order.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HistoricalSaleDTO(
        Long productId,
        String productName,
        Long fridgeId,
        Integer quantity,
        BigDecimal totalAmount,
        LocalDate date
) {}

