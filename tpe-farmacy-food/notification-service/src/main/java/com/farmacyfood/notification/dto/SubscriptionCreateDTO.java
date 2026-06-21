package com.farmacyfood.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubscriptionCreateDTO(
        @NotNull Long userId,
        @NotBlank String deviceToken,
        List<Long> productPreferences,
        List<Long> heladeraIds
) {}
