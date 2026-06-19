package com.farmacyfood.notification.controller;

import com.farmacyfood.notification.dto.NotificationResponseDTO;
import com.farmacyfood.notification.dto.SendNotificationRequest;
import com.farmacyfood.notification.dto.SubscriptionCreateDTO;
import com.farmacyfood.notification.dto.SubscriptionResponseDTO;
import com.farmacyfood.notification.dto.SubscriptionUpdateDTO;
import com.farmacyfood.notification.service.NotificationService;
import com.farmacyfood.notification.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@Tag(name = "Notification Service", description = "Push alerts and subscriptions")
public class NotificationController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Crear o actualizar suscripcion de un usuario")
    @PostMapping("/suscribir")
    public ResponseEntity<SubscriptionResponseDTO> suscribir(@Valid @RequestBody SubscriptionCreateDTO dto) {
        SubscriptionResponseDTO response = subscriptionService.crearOActualizar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar suscripcion parcialmente")
    @PutMapping("/suscribir/{userId}")
    public ResponseEntity<SubscriptionResponseDTO> actualizarSuscripcion(
            @PathVariable Long userId,
            @RequestBody SubscriptionUpdateDTO dto) {
        SubscriptionResponseDTO response = subscriptionService.actualizar(userId, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener suscripcion de un usuario")
    @GetMapping("/suscribir/{userId}")
    public ResponseEntity<SubscriptionResponseDTO> obtenerSuscripcion(@PathVariable Long userId) {
        SubscriptionResponseDTO response = subscriptionService.obtenerPorUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Recibir notificacion de heladera reconectada")
    @PostMapping("/enviar")
    public ResponseEntity<Void> enviarNotificaciones(@Valid @RequestBody SendNotificationRequest request) {
        notificationService.enviarNotificaciones(request.fridgeId(), request.productIds());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Obtener notificaciones de un usuario")
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> obtenerPorUsuario(@PathVariable Long userId) {
        List<NotificationResponseDTO> notificaciones = notificationService.obtenerPorUserId(userId);
        return ResponseEntity.ok(notificaciones);
    }

    @Operation(summary = "Marcar notificacion como leida")
    @PutMapping("/{id}/leer")
    public ResponseEntity<NotificationResponseDTO> marcarComoLeida(@PathVariable String id) {
        NotificationResponseDTO response = notificationService.marcarComoLeida(id);
        return ResponseEntity.ok(response);
    }
}
