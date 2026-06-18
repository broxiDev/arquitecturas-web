package com.farmacyfood.notification.service;

import com.farmacyfood.notification.dto.SubscriptionCreateDTO;
import com.farmacyfood.notification.dto.SubscriptionResponseDTO;
import com.farmacyfood.notification.dto.SubscriptionUpdateDTO;
import com.farmacyfood.notification.entity.Subscription;
import com.farmacyfood.notification.exception.SubscriptionNotFoundException;
import com.farmacyfood.notification.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository repository;

    public SubscriptionServiceImpl(SubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public SubscriptionResponseDTO crearOActualizar(SubscriptionCreateDTO dto) {
        Optional<Subscription> existente = repository.findByUserId(dto.userId());

        if (existente.isPresent()) {
            Subscription suscripcion = existente.get();
            suscripcion.setDeviceToken(dto.deviceToken());
            suscripcion.setProductPreferences(dto.productPreferences());
            suscripcion.setUpdatedAt(LocalDateTime.now());
            return SubscriptionResponseDTO.from(repository.save(suscripcion));
        }

        Subscription nueva = new Subscription(dto.userId(), dto.deviceToken(), dto.productPreferences());
        return SubscriptionResponseDTO.from(repository.save(nueva));
    }

    @Override
    public SubscriptionResponseDTO actualizar(Long userId, SubscriptionUpdateDTO dto) {
        Subscription suscripcion = repository.findByUserId(userId)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Suscripción no encontrada para userId: " + userId));

        if (dto.deviceToken() != null) {
            suscripcion.setDeviceToken(dto.deviceToken());
        }
        if (dto.productPreferences() != null) {
            suscripcion.setProductPreferences(dto.productPreferences());
        }
        suscripcion.setUpdatedAt(LocalDateTime.now());

        return SubscriptionResponseDTO.from(repository.save(suscripcion));
    }

    @Override
    public SubscriptionResponseDTO obtenerPorUserId(Long userId) {
        Subscription suscripcion = repository.findByUserId(userId)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Suscripción no encontrada para userId: " + userId));
        return SubscriptionResponseDTO.from(suscripcion);
    }
}
