package com.farmacyfood.notification.service;

import com.farmacyfood.notification.dto.SubscriptionCreateDTO;
import com.farmacyfood.notification.dto.SubscriptionResponseDTO;
import com.farmacyfood.notification.dto.SubscriptionUpdateDTO;

public interface SubscriptionService {

    SubscriptionResponseDTO crearOActualizar(SubscriptionCreateDTO dto);

    SubscriptionResponseDTO actualizar(Long userId, SubscriptionUpdateDTO dto);

    SubscriptionResponseDTO obtenerPorUserId(Long userId);
}
