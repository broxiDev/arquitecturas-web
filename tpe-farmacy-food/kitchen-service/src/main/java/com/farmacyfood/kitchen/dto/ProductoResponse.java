package com.farmacyfood.kitchen.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductoResponse(
    Long id,
    String name,
    String description,
    String dietaryCategory,
    BigDecimal price,
    String imageUrl,
    String nutritionalInfo,
    BigDecimal conservacionTemperature,
    String cocinaId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
