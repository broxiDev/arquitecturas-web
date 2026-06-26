package com.farmacyfood.audit.service;

import com.farmacyfood.audit.dto.AuditEventFilterDTO;
import com.farmacyfood.audit.dto.AuditEventRequest;
import com.farmacyfood.audit.dto.AuditEventResponse;
import com.farmacyfood.audit.entity.AuditEvent;
import com.farmacyfood.audit.exception.AuditEventNotFoundException;
import com.farmacyfood.audit.repository.AuditEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    @Autowired
    private AuditEventRepository repository;

    @Override
    public AuditEventResponse registrarEvento(AuditEventRequest request) {
        AuditEvent event = new AuditEvent(
                request.serviceName(),
                request.entityType(),
                request.entityId(),
                request.action(),
                request.performedBy(),
                request.details(),
                request.timestamp(),
                request.ipAddress(),
                request.correlationId()
        );
        event = repository.save(event);
        log.info("Evento de auditoría registrado: {}/{} - {} (id={})",
                event.getServiceName(), event.getEntityType(), event.getAction(), event.getId());
        return AuditEventResponse.from(event);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditEventResponse obtenerPorId(Long id) {
        AuditEvent event = repository.findById(id)
                .orElseThrow(() -> new AuditEventNotFoundException(id));
        return AuditEventResponse.from(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEventResponse> listarEventos(AuditEventFilterDTO filtro) {
        PageRequest pageRequest = PageRequest.of(filtro.page(), filtro.size());
        return repository.search(
                filtro.serviceName(),
                filtro.entityType(),
                filtro.entityId(),
                filtro.action(),
                filtro.from(),
                filtro.to(),
                pageRequest
        ).map(AuditEventResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> obtenerPorEntidad(String entityType, String entityId) {
        return repository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId)
                .stream()
                .map(AuditEventResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> obtenerPorServicio(String serviceName) {
        return repository.findByServiceNameOrderByTimestampDesc(serviceName)
                .stream()
                .map(AuditEventResponse::from)
                .toList();
    }

    @Override
    public void purgarEventosAntiguos(LocalDateTime before) {
        long count = repository.count();
        repository.deleteByTimestampBefore(before);
        long remaining = repository.count();
        log.info("Eventos de auditoría purgados: {} eliminados, {} restantes", count - remaining, remaining);
    }
}
