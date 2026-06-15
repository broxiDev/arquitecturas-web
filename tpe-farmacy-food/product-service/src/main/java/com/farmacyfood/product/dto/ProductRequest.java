package com.farmacyfood.product.dto;

import java.math.BigDecimal;

public record ProductRequest(
    String name,
    String description,
    String dietaryCategory,
    BigDecimal price,
    String imageUrl
) {}
