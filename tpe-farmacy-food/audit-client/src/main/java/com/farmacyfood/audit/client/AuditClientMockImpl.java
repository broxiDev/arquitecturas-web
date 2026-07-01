package com.farmacyfood.audit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Mock del cliente de auditoría para desarrollo local.
 * Solo loguea el evento sin enviarlo a ningún lado.
 * Se activa con el perfil "dev".
 */
@Slf4j
@Component
@Profile("dev")
public class AuditClientMockImpl implements AuditClient {

    @Override
    public void registrarEvento(AuditEventRequestDTO request) {
        log.info("[MOCK AUDIT] service={} | type={} | action={} | message={} | detail={}",
                request.serviceName(),
                request.eventType(),
                request.action(),
                request.message(),
                request.detail());
    }
}
