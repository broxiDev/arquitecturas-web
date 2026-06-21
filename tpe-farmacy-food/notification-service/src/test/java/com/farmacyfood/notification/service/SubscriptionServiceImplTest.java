package com.farmacyfood.notification.service;

import com.farmacyfood.notification.dto.SubscriptionCreateDTO;
import com.farmacyfood.notification.dto.SubscriptionResponseDTO;
import com.farmacyfood.notification.dto.SubscriptionUpdateDTO;
import com.farmacyfood.notification.entity.Subscription;
import com.farmacyfood.notification.exception.SubscriptionNotFoundException;
import com.farmacyfood.notification.repository.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository repository;

    @InjectMocks
    private SubscriptionServiceImpl service;

    @Test
    void crearOActualizar_cuandoNoExiste_creaNueva() {
        SubscriptionCreateDTO dto = new SubscriptionCreateDTO(100L, "device-abc", List.of(1L, 5L), null);

        when(repository.findByUserId(100L)).thenReturn(Optional.empty());
        when(repository.save(any(Subscription.class))).thenAnswer(invocation -> {
            Subscription saved = invocation.getArgument(0);
            saved.setId("new-id");
            return saved;
        });

        SubscriptionResponseDTO result = service.crearOActualizar(dto);

        assertEquals("new-id", result.id());
        assertEquals(100L, result.userId());
        assertEquals("device-abc", result.deviceToken());
        assertTrue(result.productPreferences().containsAll(List.of(1L, 5L)));
        verify(repository).save(any(Subscription.class));
    }

    @Test
    void crearOActualizar_cuandoExiste_actualiza() {
        SubscriptionCreateDTO dto = new SubscriptionCreateDTO(100L, "device-new", List.of(2L), null);
        Subscription existing = new Subscription(100L, "device-old", List.of(1L), null);
        existing.setId("sub1");

        when(repository.findByUserId(100L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        SubscriptionResponseDTO result = service.crearOActualizar(dto);

        assertEquals("device-new", result.deviceToken());
        assertEquals(List.of(2L), result.productPreferences());
        verify(repository).save(existing);
    }

    @Test
    void actualizar_cuandoExiste() {
        Subscription existing = new Subscription(100L, "device-old", List.of(1L), null);
        existing.setId("sub1");
        SubscriptionUpdateDTO dto = new SubscriptionUpdateDTO("device-new", List.of(2L, 3L), null);

        when(repository.findByUserId(100L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        SubscriptionResponseDTO result = service.actualizar(100L, dto);

        assertEquals("device-new", result.deviceToken());
        assertEquals(List.of(2L, 3L), result.productPreferences());
        verify(repository).save(existing);
    }

    @Test
    void actualizar_cuandoNoExiste_lanzaExcepcion() {
        SubscriptionUpdateDTO dto = new SubscriptionUpdateDTO("device-new", List.of(2L), null);

        when(repository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThrows(SubscriptionNotFoundException.class, () -> service.actualizar(99L, dto));
        verify(repository, never()).save(any());
    }

    @Test
    void actualizar_soloDeviceToken() {
        Subscription existing = new Subscription(100L, "device-old", List.of(1L), null);
        existing.setId("sub1");
        SubscriptionUpdateDTO dto = new SubscriptionUpdateDTO("device-new", null, null);

        when(repository.findByUserId(100L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        SubscriptionResponseDTO result = service.actualizar(100L, dto);

        assertEquals("device-new", result.deviceToken());
        assertEquals(List.of(1L), result.productPreferences());
    }

    @Test
    void actualizar_soloProductPreferences() {
        Subscription existing = new Subscription(100L, "device-old", List.of(1L), null);
        existing.setId("sub1");
        SubscriptionUpdateDTO dto = new SubscriptionUpdateDTO(null, List.of(2L, 3L), null);

        when(repository.findByUserId(100L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        SubscriptionResponseDTO result = service.actualizar(100L, dto);

        assertEquals("device-old", result.deviceToken());
        assertEquals(List.of(2L, 3L), result.productPreferences());
    }

    @Test
    void obtenerPorUserId_cuandoExiste() {
        Subscription subscription = new Subscription(100L, "device-abc", List.of(1L), null);
        subscription.setId("sub1");

        when(repository.findByUserId(100L)).thenReturn(Optional.of(subscription));

        SubscriptionResponseDTO result = service.obtenerPorUserId(100L);

        assertEquals("sub1", result.id());
        assertEquals(100L, result.userId());
    }

    @Test
    void obtenerPorUserId_cuandoNoExiste_lanzaExcepcion() {
        when(repository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThrows(SubscriptionNotFoundException.class, () -> service.obtenerPorUserId(99L));
    }
}
