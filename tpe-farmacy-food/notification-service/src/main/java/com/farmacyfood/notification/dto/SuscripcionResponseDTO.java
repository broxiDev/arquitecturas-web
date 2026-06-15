package com.farmacyfood.notification.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SuscripcionResponseDTO(
        String id,
        Long userId,
        String deviceToken,
        List<Long> productPreferences,
        LocalDateTime createdAt
) {}
