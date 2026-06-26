package com.farmacyfood.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AuditEventRequest(
        @NotBlank(message = "serviceName es obligatorio")
        String serviceName,

        @NotBlank(message = "entityType es obligatorio")
        String entityType,

        @NotBlank(message = "entityId es obligatorio")
        String entityId,

        @NotBlank(message = "action es obligatorio")
        String action,

        String performedBy,

        String details,

        LocalDateTime timestamp,

        String ipAddress,

        String correlationId
) {}
