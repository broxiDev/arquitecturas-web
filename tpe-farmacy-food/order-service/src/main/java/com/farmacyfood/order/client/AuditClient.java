package com.farmacyfood.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "audit-service", url = "${clients.audit-service.url:http://localhost:8088}")
@Profile("!dev")
public interface AuditClient {

    @PostMapping("/api/v1/auditoria/eventos")
    void registrarEvento(@RequestBody AuditEventRequestDTO requestDTO);
}
