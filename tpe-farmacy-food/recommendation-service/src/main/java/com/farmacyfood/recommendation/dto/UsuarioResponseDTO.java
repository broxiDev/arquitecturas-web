package com.farmacyfood.recommendation.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UsuarioResponseDTO(
        Long id,
        String name,
        String email,
        List<String> dietaryPreferences,
        LocalDateTime createdAt
) {}
