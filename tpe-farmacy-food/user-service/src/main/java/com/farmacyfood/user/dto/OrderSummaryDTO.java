package com.farmacyfood.user.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderSummaryDTO(
        Long orderId,
        Long userId,
        Long fridgeId,
        List<OrderItemSummaryDTO> items,
        double total,
        String status,
        String paymentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
