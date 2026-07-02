package com.farmacyfood.audit.service;

import com.farmacyfood.audit.dto.AuditEventRequest;
import com.farmacyfood.audit.dto.AuditEventResponse;

import java.util.List;

public interface AuditService {

    AuditEventResponse registrarEvento(AuditEventRequest request);

    List<AuditEventResponse> listarEventos(String serviceName);

    AuditEventResponse obtenerEvento(Long id);
}
