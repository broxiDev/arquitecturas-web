package com.farmacyfood.audit.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Implementación real del cliente de auditoría vía Feign.
 * Se activa con cualquier perfil que NO sea "dev".
 * La URL se configura con la propiedad {@code clients.audit-service.url}.
 */
@FeignClient(name = "audit-service", url = "${clients.audit-service.url:http://localhost:8088}")
@Profile("!dev")
public interface AuditClientFeign extends AuditClient {

    @Override
    @PostMapping("/api/v1/auditoria/eventos")
    void registrarEvento(@RequestBody AuditEventRequestDTO request);
}
