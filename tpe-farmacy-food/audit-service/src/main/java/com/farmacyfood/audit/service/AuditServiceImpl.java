package com.farmacyfood.audit.service;

import com.farmacyfood.audit.dto.AuditEventRequest;
import com.farmacyfood.audit.dto.AuditEventResponse;
import com.farmacyfood.audit.entity.AuditEvent;
import com.farmacyfood.audit.repository.AuditEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                request.request(),
                request.response()
        );
        event = repository.save(event);
        log.info("Evento de auditoría registrado: service={} | id={}",
                event.getServiceName(), event.getId());
        return AuditEventResponse.from(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> listarEventos(String serviceName) {
        List<AuditEvent> eventos;
        if (serviceName != null && !serviceName.isBlank()) {
            eventos = repository.findByServiceNameOrderByTimestampDesc(serviceName);
        } else {
            eventos = repository.findAll();
        }
        return eventos.stream()
                .map(AuditEventResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuditEventResponse obtenerEvento(Long id) {
        AuditEvent event = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento de auditoría no encontrado: " + id));
        return AuditEventResponse.from(event);
    }
}
