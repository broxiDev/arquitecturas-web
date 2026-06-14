package com.farmacyfood.kitchen.dto;

public record ItemPlanDTO(
    Long productId,
    String productName,
    Integer suggestedQuantity
) {}
