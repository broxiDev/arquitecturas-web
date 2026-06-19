package com.farmacyfood.kitchen.dto;

import java.math.BigDecimal;

public record ProductoRequest(
    String name,
    String description,
    String dietaryCategory,
    BigDecimal price,
    String imageUrl,
    String nutritionalInfo,
    BigDecimal conservacionTemperature
) {}
