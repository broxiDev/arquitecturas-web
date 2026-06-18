package com.farmacyfood.order.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(Long orderId,
                               Long userId,
                               Long fridgeId,
                               List<OrderItemDTO> items,
                               double total,
                               String status,
                               String paymentId,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt
                               ) {
    //Es el espejo de la entidad Order pero sin exponer datos internos (no hay relaciones JPA, todo plano)
    // Se devuelve al cliente después de crear, pagar, cancelar, etc.
}
