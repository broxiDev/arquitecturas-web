package com.farmacyfood.notification.dto;

import java.util.List;

public record SubscriptionUpdateDTO(
        String deviceToken,
        List<Long> productPreferences,
        List<Long> heladeraIds
) {}
