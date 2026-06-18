package com.farmacyfood.notification.dto;

import com.farmacyfood.notification.entity.Notification;

import java.time.LocalDateTime;

public record NotificationResponseDTO(
        String id,
        Long userId,
        Long productId,
        Long fridgeId,
        String message,
        Boolean read,
        LocalDateTime sentAt
) {
    public static NotificationResponseDTO from(Notification notificacion) {
        return new NotificationResponseDTO(
                notificacion.getId(),
                notificacion.getUserId(),
                notificacion.getProductId(),
                notificacion.getFridgeId(),
                notificacion.getMessage(),
                notificacion.getRead(),
                notificacion.getSentAt()
        );
    }
}
