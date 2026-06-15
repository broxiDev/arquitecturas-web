package com.farmacyfood.notification.dto;

import java.util.List;

public record SuscripcionUpdateDTO(
        String deviceToken,
        List<Long> productPreferences
) {}
