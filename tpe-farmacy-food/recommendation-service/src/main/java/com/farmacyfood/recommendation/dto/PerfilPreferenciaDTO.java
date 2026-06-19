package com.farmacyfood.recommendation.dto;

import java.util.List;

public record PerfilPreferenciaDTO(
        Long userId,
        List<String> dietaryPreferences
) {}
