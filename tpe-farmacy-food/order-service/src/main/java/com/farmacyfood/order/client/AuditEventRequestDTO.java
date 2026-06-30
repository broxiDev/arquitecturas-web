package com.farmacyfood.order.client;

public record AuditEventRequestDTO(
        String serviceName,
        String request,
        String response
) {}
