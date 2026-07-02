package com.farmacyfood.audit.dto;

import jakarta.validation.constraints.NotBlank;

public record AuditEventRequest(

        @NotBlank(message = "serviceName es obligatorio")
        String serviceName,

        String request,

        String response
) {}
