package com.farmacyfood.notification.controller;

import com.farmacyfood.notification.dto.NotificationResponseDTO;
import com.farmacyfood.notification.dto.SubscriptionCreateDTO;
import com.farmacyfood.notification.dto.SubscriptionResponseDTO;
import com.farmacyfood.notification.dto.SubscriptionUpdateDTO;
import com.farmacyfood.notification.exception.GlobalExceptionHandler;
import com.farmacyfood.notification.exception.SubscriptionNotFoundException;
import com.farmacyfood.notification.service.NotificationService;
import com.farmacyfood.notification.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void suscribir_retorna201() throws Exception {
        SubscriptionResponseDTO response = new SubscriptionResponseDTO(
                "sub1", 100L, "device-abc", List.of(1L, 5L), LocalDateTime.now());

        when(subscriptionService.crearOActualizar(any(SubscriptionCreateDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/notificaciones/suscribir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userId": 100,
                                    "deviceToken": "device-abc",
                                    "productPreferences": [1, 5]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("sub1"))
                .andExpect(jsonPath("$.userId").value(100));
    }

    @Test
    void actualizarSuscripcion_retorna200() throws Exception {
        SubscriptionResponseDTO response = new SubscriptionResponseDTO(
                "sub1", 100L, "device-new", List.of(2L), LocalDateTime.now());

        when(subscriptionService.actualizar(eq(100L), any(SubscriptionUpdateDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/notificaciones/suscribir/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "deviceToken": "device-new",
                                    "productPreferences": [2]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceToken").value("device-new"));
    }

    @Test
    void obtenerSuscripcion_retorna200() throws Exception {
        SubscriptionResponseDTO response = new SubscriptionResponseDTO(
                "sub1", 100L, "device-abc", List.of(1L), LocalDateTime.now());

        when(subscriptionService.obtenerPorUserId(100L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/notificaciones/suscribir/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("sub1"));
    }

    @Test
    void obtenerSuscripcion_retorna404() throws Exception {
        when(subscriptionService.obtenerPorUserId(99L))
                .thenThrow(new SubscriptionNotFoundException("Suscripcion no encontrada para userId: 99"));

        mockMvc.perform(get("/api/v1/notificaciones/suscribir/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void enviarNotificaciones_retorna202() throws Exception {
        doNothing().when(notificationService).enviarNotificaciones(1L, List.of(10L, 20L));

        mockMvc.perform(post("/api/v1/notificaciones/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fridgeId": 1,
                                    "productIds": [10, 20]
                                }
                                """))
                .andExpect(status().isAccepted());

        verify(notificationService).enviarNotificaciones(1L, List.of(10L, 20L));
    }

    @Test
    void obtenerPorUsuario_retorna200() throws Exception {
        NotificationResponseDTO notif = new NotificationResponseDTO(
                "n1", 100L, 10L, 1L, "mensaje", false, LocalDateTime.now(), null);

        when(notificationService.obtenerPorUserId(100L)).thenReturn(List.of(notif));

        mockMvc.perform(get("/api/v1/notificaciones/usuario/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("n1"))
                .andExpect(jsonPath("$[0].userId").value(100));
    }

    @Test
    void marcarComoLeida_retorna200() throws Exception {
        NotificationResponseDTO response = new NotificationResponseDTO(
                "n1", 100L, 10L, 1L, "mensaje", true, LocalDateTime.now(), LocalDateTime.now());

        when(notificationService.marcarComoLeida("n1")).thenReturn(response);

        mockMvc.perform(put("/api/v1/notificaciones/n1/leer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("n1"))
                .andExpect(jsonPath("$.read").value(true));

        verify(notificationService).marcarComoLeida("n1");
    }
}
