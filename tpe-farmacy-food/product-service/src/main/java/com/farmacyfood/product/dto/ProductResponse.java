package com.farmacyfood.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
    Long id,
    String name,
    String description,
    String dietaryCategory,
    BigDecimal price,
    String imageUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
