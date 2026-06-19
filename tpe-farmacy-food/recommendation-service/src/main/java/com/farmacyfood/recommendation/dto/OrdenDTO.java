package com.farmacyfood.recommendation.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrdenDTO(
        Long orderId,
        Long userId,
        Long fridgeId,
        List<OrderItemDTO> items,
        double total,
        String status,
        String paymentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
