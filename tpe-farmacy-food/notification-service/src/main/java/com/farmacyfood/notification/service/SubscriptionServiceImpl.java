package com.farmacyfood.notification.service;


import com.farmacyfood.notification.dto.SubscriptionCreateDTO;
import com.farmacyfood.notification.dto.SubscriptionResponseDTO;
import com.farmacyfood.notification.dto.SubscriptionUpdateDTO;
import com.farmacyfood.notification.entity.Subscription;
import com.farmacyfood.notification.exception.SubscriptionNotFoundException;
import com.farmacyfood.notification.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository repository;

    @Override
    public SubscriptionResponseDTO crearOActualizar(SubscriptionCreateDTO dto) {
        try {
            Optional<Subscription> existente = repository.findByUserId(dto.userId());

            if (existente.isPresent()) {
                Subscription suscripcion = existente.get();
                suscripcion.setDeviceToken(dto.deviceToken());
                suscripcion.setProductPreferences(dto.productPreferences());
                suscripcion.setHeladeraIds(dto.heladeraIds());
                suscripcion.setUpdatedAt(LocalDateTime.now());
                return SubscriptionResponseDTO.from(repository.save(suscripcion));
            }

            Subscription nueva = new Subscription(dto.userId(), dto.deviceToken(), dto.productPreferences(), dto.heladeraIds());
            return SubscriptionResponseDTO.from(repository.save(nueva));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public SubscriptionResponseDTO actualizar(Long userId, SubscriptionUpdateDTO dto) {
        try {
            Subscription suscripcion = repository.findByUserId(userId)
                    .orElseThrow(() -> new SubscriptionNotFoundException(
                            "Suscripción no encontrada para userId: " + userId));

            if (dto.deviceToken() != null) {
                suscripcion.setDeviceToken(dto.deviceToken());
            }
            if (dto.productPreferences() != null) {
                suscripcion.setProductPreferences(dto.productPreferences());
            }
            if (dto.heladeraIds() != null) {
                suscripcion.setHeladeraIds(dto.heladeraIds());
            }
            suscripcion.setUpdatedAt(LocalDateTime.now());

            return SubscriptionResponseDTO.from(repository.save(suscripcion));
        } catch (SubscriptionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public SubscriptionResponseDTO obtenerPorUserId(Long userId) {
        Subscription suscripcion = repository.findByUserId(userId)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Suscripción no encontrada para userId: " + userId));
        return SubscriptionResponseDTO.from(suscripcion);
    }

    @Override
    public void eliminar(Long userId) {
        try {
            Subscription suscripcion = repository.findByUserId(userId)
                    .orElseThrow(() -> new SubscriptionNotFoundException(
                            "Suscripción no encontrada para userId: " + userId));
            repository.delete(suscripcion);
        } catch (SubscriptionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }
}
