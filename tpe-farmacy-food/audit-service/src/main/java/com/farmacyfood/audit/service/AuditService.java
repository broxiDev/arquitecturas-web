package com.farmacyfood.audit.service;

import com.farmacyfood.audit.dto.AuditEventRequest;
import com.farmacyfood.audit.dto.AuditEventResponse;

public interface AuditService {

    AuditEventResponse registrarEvento(AuditEventRequest request);
}
