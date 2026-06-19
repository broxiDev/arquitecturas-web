package com.farmacyfood.user.dto;

public record OrderItemSummaryDTO(
        Long productId,
        int quantity,
        double price
) {}
