package com.farmacyfood.audit.dto;

import java.time.LocalDateTime;

public record AuditEventFilterDTO(
        String serviceName,
        String entityType,
        String entityId,
        String action,
        String performedBy,
        LocalDateTime from,
        LocalDateTime to,
        int page,
        int size
) {
    public AuditEventFilterDTO {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;
    }
}
