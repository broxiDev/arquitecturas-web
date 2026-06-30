package com.farmacyfood.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
public class AuditClientMockImpl implements AuditClient {

    @Override
    public void registrarEvento(AuditEventRequestDTO requestDTO) {
        log.info("[MOCK AUDIT] requestDTO{}", requestDTO);
    }
}
