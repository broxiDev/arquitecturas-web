package com.farmacyfood.notification.service;

import com.farmacyfood.notification.dto.HeladeraStatusChangeDTO;
import com.farmacyfood.notification.dto.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {

    void enviarNotificaciones(Long fridgeId, List<Long> productIds);

    void notificarCambioEstado(HeladeraStatusChangeDTO dto);

    List<NotificationResponseDTO> obtenerPorUserId(Long userId);

    List<NotificationResponseDTO> obtenerNoLeidas(Long userId);

    NotificationResponseDTO marcarComoLeida(String id);

    void eliminar(String id);
}
