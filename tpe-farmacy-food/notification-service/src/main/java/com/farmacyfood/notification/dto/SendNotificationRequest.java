package com.farmacyfood.notification.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SendNotificationRequest(
        @NotNull Long fridgeId,
        @NotEmpty List<Long> productIds
) {}
