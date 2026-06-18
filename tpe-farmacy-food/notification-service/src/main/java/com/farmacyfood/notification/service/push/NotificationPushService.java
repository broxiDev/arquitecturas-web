package com.farmacyfood.notification.service.push;

public interface NotificationPushService {

    void enviarPush(String deviceToken, String message);
}
