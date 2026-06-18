package com.farmacyfood.notification.service;

import com.farmacyfood.notification.dto.NotificationResponseDTO;
import com.farmacyfood.notification.entity.Notification;
import com.farmacyfood.notification.entity.Subscription;
import com.farmacyfood.notification.exception.NotificationNotFoundException;
import com.farmacyfood.notification.repository.NotificationRepository;
import com.farmacyfood.notification.repository.SubscriptionRepository;
import com.farmacyfood.notification.service.push.NotificationPushService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificacionRepository;
    private final SubscriptionRepository suscripcionRepository;
    private final NotificationPushService pushService;

    public NotificationServiceImpl(NotificationRepository notificacionRepository,
                                   SubscriptionRepository suscripcionRepository,
                                   NotificationPushService pushService) {
        this.notificacionRepository = notificacionRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.pushService = pushService;
    }

    @Override
    public void enviarNotificationes(Long fridgeId, List<Long> productIds) {
        for (Long productId : productIds) {
            List<Subscription> suscriptores = suscripcionRepository.findByProductPreferencesContaining(productId);
            for (Subscription suscriptor : suscriptores) {
                String message = "El producto " + productId + " ya esta disponible en la heladera " + fridgeId;
                Notification notificacion = new Notification(suscriptor.getUserId(), productId, fridgeId, message);
                notificacionRepository.save(notificacion);
                pushService.enviarPush(suscriptor.getDeviceToken(), message);
            }
        }
    }

    @Override
    public List<NotificationResponseDTO> obtenerPorUserId(Long userId) {
        return notificacionRepository.findByUserIdOrderBySentAtDesc(userId).stream()
                .map(NotificationResponseDTO::from)
                .toList();
    }

    @Override
    public List<NotificationResponseDTO> obtenerNoLeidas(Long userId) {
        return notificacionRepository.findByUserIdAndReadOrderBySentAtDesc(userId, false).stream()
                .map(NotificationResponseDTO::from)
                .toList();
    }

    @Override
    public void marcarComoLeida(String id) {
        Notification notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification no encontrada con id: " + id));
        notificacion.setRead(true);
        notificacion.setReadAt(LocalDateTime.now());
        notificacionRepository.save(notificacion);
    }
}
