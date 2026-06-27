package com.farmacyfood.audit.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record AuditEventRequest(

        @NotBlank(message = "serviceName es obligatorio")
        String serviceName,

        String request,

        String response,

        LocalDateTime timestamp
) {}
