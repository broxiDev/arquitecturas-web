package com.farmacyfood.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificacionSendDTO(
        @NotNull Long userId,
        @NotNull Long productId,
        @NotNull Long fridgeId,
        @NotBlank String message
) {}
