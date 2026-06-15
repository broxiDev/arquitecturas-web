package com.farmacyfood.notification.dto;

import java.time.LocalDateTime;

public record NotificacionResponseDTO(
        String id,
        Long userId,
        Long productId,
        Long fridgeId,
        String message,
        Boolean read,
        LocalDateTime sentAt
) {}
