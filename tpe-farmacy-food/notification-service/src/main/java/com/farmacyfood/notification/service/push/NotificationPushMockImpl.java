package com.farmacyfood.notification.service.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationPushMockImpl implements NotificationPushService {

    private static final Logger log = LoggerFactory.getLogger(NotificationPushMockImpl.class);

    @Override
    public void enviarPush(String deviceToken, String message) {
        log.info("Enviando push a dispositivo {}: {}", deviceToken, message);
    }
}
