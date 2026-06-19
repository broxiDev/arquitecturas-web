package com.farmacyfood.recommendation.dto;

public record OrderItemDTO(
        Long productId,
        String productName,
        Integer quantity,
        double unitPrice
) {}
