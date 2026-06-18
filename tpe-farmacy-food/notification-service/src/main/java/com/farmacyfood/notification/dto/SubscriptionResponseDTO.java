package com.farmacyfood.notification.dto;

import com.farmacyfood.notification.entity.Subscription;

import java.time.LocalDateTime;
import java.util.List;

public record SubscriptionResponseDTO(
        String id,
        Long userId,
        String deviceToken,
        List<Long> productPreferences,
        LocalDateTime createdAt
) {
    public static SubscriptionResponseDTO from(Subscription suscripcion) {
        return new SubscriptionResponseDTO(
                suscripcion.getId(),
                suscripcion.getUserId(),
                suscripcion.getDeviceToken(),
                suscripcion.getProductPreferences(),
                suscripcion.getCreatedAt()
        );
    }
}
