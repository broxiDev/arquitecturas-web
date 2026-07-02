package com.farmacyfood.audit.dto;

import com.farmacyfood.audit.entity.AuditEvent;

import java.time.LocalDateTime;

public record AuditEventResponse(
        Long id,
        String serviceName,
        String request,
        String response,
        LocalDateTime timestamp
) {
    public static AuditEventResponse from(AuditEvent event) {
        return new AuditEventResponse(
                event.getId(),
                event.getServiceName(),
                event.getRequest(),
                event.getResponse(),
                event.getTimestamp()
        );
    }
}
