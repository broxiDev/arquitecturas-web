package com.farmacyfood.notification.service;

import com.farmacyfood.audit.client.AuditLogger;
import com.farmacyfood.notification.constants.AuditMessages;
import com.farmacyfood.notification.dto.HeladeraStatusChangeDTO;
import com.farmacyfood.notification.dto.NotificationResponseDTO;
import com.farmacyfood.notification.entity.Notification;
import com.farmacyfood.notification.entity.Subscription;
import com.farmacyfood.notification.exception.NotificationNotFoundException;
import com.farmacyfood.notification.repository.NotificationRepository;
import com.farmacyfood.notification.repository.SubscriptionRepository;
import com.farmacyfood.notification.service.push.NotificationPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private NotificationPushService pushService;

    @Autowired
    private AuditLogger auditLogger;

    @Override
    public void enviarNotificaciones(Long fridgeId, List<Long> productIds) {
        try {
            for (Long productId : productIds) {
                List<Subscription> subscriptions = subscriptionRepository.findByProductPreferencesContaining(productId);
                for (Subscription subscription : subscriptions) {
                    String message = "El producto " + productId + " ya esta disponible en la heladera " + fridgeId;
                    Notification notification = new Notification(subscription.getUserId(), productId, fridgeId, message);
                    notificationRepository.save(notification);
                    pushService.enviarPush(subscription.getDeviceToken(), message);
                }
            }
            auditLogger.success("NOTIFY_AVAILABILITY", AuditMessages.AVAILABILITY_NOTIFIED,
                    "fridgeId: " + fridgeId + ", products: " + productIds);
        } catch (Exception e) {
            auditLogger.error("NOTIFY_AVAILABILITY", "Error al enviar notificaciones de disponibilidad", e.getMessage());
            log.error("Error al enviar notificaciones", e);
        }
    }

    private static final Map<String, String> STATUS_TRANSLATIONS = Map.of(
            "ACTIVE", "ACTIVA",
            "OUT_OF_SERVICE", "FUERA DE SERVICIO",
            "MAINTENANCE", "EN MANTENIMIENTO"
    );

    @Override
    public void notificarCambioEstado(HeladeraStatusChangeDTO dto) {
        try {
            String statusLabel = STATUS_TRANSLATIONS.getOrDefault(dto.newStatus(), dto.newStatus());
            String message = "Heladera '" + dto.heladeraName() + "' cambió a estado: " + statusLabel;

            List<Subscription> admins = subscriptionRepository.findByHeladeraIdsContaining(dto.heladeraId());

            for (Subscription admin : admins) {
                Notification notification = new Notification(admin.getUserId(), null, dto.heladeraId(), message);
                notificationRepository.save(notification);
                pushService.enviarPush(admin.getDeviceToken(), message);
            }
            auditLogger.success("STATUS_CHANGE", AuditMessages.STATUS_CHANGE_NOTIFIED,
                    "heladeraId: " + dto.heladeraId() + ", newStatus: " + dto.newStatus());
        } catch (Exception e) {
            auditLogger.error("STATUS_CHANGE", "Error al notificar cambio de estado", e.getMessage());
            log.error("Error al notificar cambio de estado", e);
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
    public NotificationResponseDTO marcarComoLeida(String id) {
        try {
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new NotificationNotFoundException("Notification no encontrada con id: " + id));
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            NotificationResponseDTO response = NotificationResponseDTO.from(notificationRepository.save(notification));
            auditLogger.success("MARK_READ", AuditMessages.NOTIFICATION_MARKED_READ, "notificationId: " + id);
            return response;
        } catch (NotificationNotFoundException e) {
            auditLogger.error("MARK_READ", AuditMessages.NOTIFICATION_NOT_FOUND, "id: " + id);
            throw e;
        } catch (Exception e) {
            auditLogger.error("MARK_READ", "Error al marcar notificación como leída", e.getMessage());
            throw e;
        }
    }

    @Override
    public void eliminar(String id) {
        try {
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new NotificationNotFoundException("Notification no encontrada con id: " + id));
            notificationRepository.delete(notification);
            auditLogger.success("DELETE_NOTIFICATION", AuditMessages.NOTIFICATION_DELETED, "id: " + id);
        } catch (NotificationNotFoundException e) {
            auditLogger.error("DELETE_NOTIFICATION", AuditMessages.NOTIFICATION_NOT_FOUND, "id: " + id);
            throw e;
        } catch (Exception e) {
            auditLogger.error("DELETE_NOTIFICATION", "Error al eliminar notificación", e.getMessage());
            throw e;
        }
    }
}
