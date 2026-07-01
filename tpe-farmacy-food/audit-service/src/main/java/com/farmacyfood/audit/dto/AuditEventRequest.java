package com.farmacyfood.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AuditEventRequest(

        @NotBlank(message = "serviceName es obligatorio")
        String serviceName,

        @NotBlank(message = "eventType es obligatorio (SUCCESS, ERROR, PENDING, INFO)")
        String eventType,

        @NotBlank(message = "action es obligatorio")
        String action,

        String message,

        String request,

        String response,

        LocalDateTime timestamp
) {}
