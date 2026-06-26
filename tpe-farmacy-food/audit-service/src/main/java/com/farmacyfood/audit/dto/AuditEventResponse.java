package com.farmacyfood.audit.dto;

import com.farmacyfood.audit.entity.AuditEvent;

import java.time.LocalDateTime;

public record AuditEventResponse(
        Long id,
        String serviceName,
        String entityType,
        String entityId,
        String action,
        String performedBy,
        String details,
        LocalDateTime timestamp,
        String ipAddress,
        String correlationId
) {
    public static AuditEventResponse from(AuditEvent event) {
        return new AuditEventResponse(
                event.getId(),
                event.getServiceName(),
                event.getEntityType(),
                event.getEntityId(),
                event.getAction(),
                event.getPerformedBy(),
                event.getDetails(),
                event.getTimestamp(),
                event.getIpAddress(),
                event.getCorrelationId()
        );
    }
}
