package com.farmacyfood.notification.service;

import com.farmacyfood.notification.dto.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {

    void enviarNotificaciones(Long fridgeId, List<Long> productIds);

    List<NotificationResponseDTO> obtenerPorUserId(Long userId);

    List<NotificationResponseDTO> obtenerNoLeidas(Long userId);

    void marcarComoLeida(String id);
}
