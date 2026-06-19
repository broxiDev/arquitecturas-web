package com.farmacyfood.notification.service;

import com.farmacyfood.notification.dto.NotificationResponseDTO;
import com.farmacyfood.notification.entity.Notification;
import com.farmacyfood.notification.entity.Subscription;
import com.farmacyfood.notification.exception.NotificationNotFoundException;
import com.farmacyfood.notification.repository.NotificationRepository;
import com.farmacyfood.notification.repository.SubscriptionRepository;
import com.farmacyfood.notification.service.push.NotificationPushService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private NotificationPushService pushService;

    @InjectMocks
    private NotificationServiceImpl service;

    @Test
    void enviarNotificaciones_cuandoHaySuscriptores_creaNotificacionesYEnviaPush() {
        Long fridgeId = 1L;
        List<Long> productIds = List.of(10L);
        Subscription subscription = new Subscription(100L, "device-abc", List.of(10L));
        subscription.setId("sub1");

        when(subscriptionRepository.findByProductPreferencesContaining(10L))
                .thenReturn(List.of(subscription));

        service.enviarNotificaciones(fridgeId, productIds);

        verify(subscriptionRepository).findByProductPreferencesContaining(10L);
        verify(notificationRepository).save(any(Notification.class));
        verify(pushService).enviarPush("device-abc", "El producto 10 ya esta disponible en la heladera 1");
    }

    @Test
    void enviarNotificaciones_cuandoNoHaySuscriptores_noHaceNada() {
        when(subscriptionRepository.findByProductPreferencesContaining(10L))
                .thenReturn(List.of());

        service.enviarNotificaciones(1L, List.of(10L));

        verify(notificationRepository, never()).save(any());
        verify(pushService, never()).enviarPush(any(), any());
    }

    @Test
    void enviarNotificaciones_conMultiplesProductos() {
        Long fridgeId = 1L;
        Subscription sub = new Subscription(100L, "device-abc", List.of(10L, 20L));
        sub.setId("sub1");

        when(subscriptionRepository.findByProductPreferencesContaining(10L))
                .thenReturn(List.of(sub));
        when(subscriptionRepository.findByProductPreferencesContaining(20L))
                .thenReturn(List.of(sub));

        service.enviarNotificaciones(fridgeId, List.of(10L, 20L));

        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(pushService, times(2)).enviarPush(any(), any());
    }

    @Test
    void obtenerPorUserId_retornaNotificaciones() {
        Notification notif = new Notification(100L, 10L, 1L, "mensaje");
        notif.setId("n1");

        when(notificationRepository.findByUserIdOrderBySentAtDesc(100L))
                .thenReturn(List.of(notif));

        List<NotificationResponseDTO> result = service.obtenerPorUserId(100L);

        assertEquals(1, result.size());
        assertEquals("n1", result.getFirst().id());
        assertEquals(100L, result.getFirst().userId());
    }

    @Test
    void obtenerPorUserId_cuandoNoHay_retornaVacio() {
        when(notificationRepository.findByUserIdOrderBySentAtDesc(99L))
                .thenReturn(List.of());

        List<NotificationResponseDTO> result = service.obtenerPorUserId(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void obtenerNoLeidas_retornaSoloNoLeidas() {
        Notification notif = new Notification(100L, 10L, 1L, "mensaje");
        notif.setId("n1");

        when(notificationRepository.findByUserIdAndReadOrderBySentAtDesc(100L, false))
                .thenReturn(List.of(notif));

        List<NotificationResponseDTO> result = service.obtenerNoLeidas(100L);

        assertEquals(1, result.size());
        assertFalse(result.getFirst().read());
    }

    @Test
    void marcarComoLeida_cuandoExiste() {
        Notification notif = new Notification(100L, 10L, 1L, "mensaje");
        notif.setId("n1");

        when(notificationRepository.findById("n1")).thenReturn(Optional.of(notif));
        when(notificationRepository.save(notif)).thenReturn(notif);

        NotificationResponseDTO result = service.marcarComoLeida("n1");

        assertTrue(result.read());
        assertNotNull(notif.getReadAt());
        assertEquals("n1", result.id());
        verify(notificationRepository).save(notif);
    }

    @Test
    void marcarComoLeida_cuandoNoExiste_lanzaExcepcion() {
        when(notificationRepository.findById("not-found")).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class,
                () -> service.marcarComoLeida("not-found"));
        verify(notificationRepository, never()).save(any());
    }
}
