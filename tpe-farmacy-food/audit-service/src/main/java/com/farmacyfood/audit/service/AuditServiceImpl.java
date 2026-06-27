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
                request.response(),
                request.timestamp()
        );
        event = repository.save(event);
        log.info("Evento de auditoría registrado: service={} | id={}", event.getServiceName(), event.getId());
        return AuditEventResponse.from(event);
    }
}
