package com.farmacyfood.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HeladeraStatusChangeDTO(
        @NotNull Long heladeraId,
        @NotBlank String heladeraName,
        @NotBlank String newStatus,
        @NotBlank String oldStatus
) {}
