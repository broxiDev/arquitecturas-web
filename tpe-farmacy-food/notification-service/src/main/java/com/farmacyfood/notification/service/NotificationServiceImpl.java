package com.farmacyfood.notification.service;

import com.farmacyfood.notification.dto.NotificationResponseDTO;
import com.farmacyfood.notification.entity.Notification;
import com.farmacyfood.notification.entity.Subscription;
import com.farmacyfood.notification.exception.NotificationNotFoundException;
import com.farmacyfood.notification.repository.NotificationRepository;
import com.farmacyfood.notification.repository.SubscriptionRepository;
import com.farmacyfood.notification.service.push.NotificationPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private NotificationPushService pushService;

    @Override
    public void enviarNotificaciones(Long fridgeId, List<Long> productIds) {
        for (Long productId : productIds) {
            List<Subscription> subscriptions = subscriptionRepository.findByProductPreferencesContaining(productId);
            for (Subscription subscription : subscriptions) {
                String message = "El producto " + productId + " ya esta disponible en la heladera " + fridgeId;
                Notification notification = new Notification(subscription.getUserId(), productId, fridgeId, message);
                notificationRepository.save(notification);
                pushService.enviarPush(subscription.getDeviceToken(), message);
            }
        }
    }

    @Override
    public List<NotificationResponseDTO> obtenerPorUserId(Long userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId).stream()
                .map(NotificationResponseDTO::from)
                .toList();
    }

    @Override
    public List<NotificationResponseDTO> obtenerNoLeidas(Long userId) {
        return notificationRepository.findByUserIdAndReadOrderBySentAtDesc(userId, false).stream()
                .map(NotificationResponseDTO::from)
                .toList();
    }

    @Override
    public void marcarComoLeida(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification no encontrada con id: " + id));
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}
