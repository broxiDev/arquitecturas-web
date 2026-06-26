package com.farmacyfood.audit.service;

import com.farmacyfood.audit.dto.AuditEventFilterDTO;
import com.farmacyfood.audit.dto.AuditEventRequest;
import com.farmacyfood.audit.dto.AuditEventResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AuditService {

    AuditEventResponse registrarEvento(AuditEventRequest request);

    AuditEventResponse obtenerPorId(Long id);

    Page<AuditEventResponse> listarEventos(AuditEventFilterDTO filtro);

    List<AuditEventResponse> obtenerPorEntidad(String entityType, String entityId);

    List<AuditEventResponse> obtenerPorServicio(String serviceName);

    void purgarEventosAntiguos(java.time.LocalDateTime before);
}
