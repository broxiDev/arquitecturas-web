package com.farmacyfood.notification.service.push;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class NotificationPushMockImpl implements NotificationPushService {

    private static final Logger log = LoggerFactory.getLogger(NotificationPushMockImpl.class);
    private static final Random random = new Random();

    @Override
    @CircuitBreaker(name = "pushService", fallbackMethod = "fallbackPush")
    public void enviarPush(String deviceToken, String message) {
        if (random.nextDouble() < 0.5) {
            log.warn("PUSH FALLIDO (simulado) para dispositivo {} — servicio de push no responde", deviceToken);
            throw new RuntimeException("Push service temporary unavailable");
        }
        log.info("PUSH ENVIADO a dispositivo {}: {}", deviceToken, message);
    }

    @SuppressWarnings("unused")
    private void fallbackPush(String deviceToken, String message, Throwable t) {
        log.error("PUSH FALLBACK para dispositivo {} — error: {}. La notificacion queda pendiente para reintentar.", deviceToken, t.getMessage());
    }
}
