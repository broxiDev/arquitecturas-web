package com.farmacyfood.notification.service;

import com.farmacyfood.audit.client.AuditLogger;
import com.farmacyfood.notification.constants.AuditMessages;
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

    @Autowired
    private AuditLogger auditLogger;

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
                SubscriptionResponseDTO response = SubscriptionResponseDTO.from(repository.save(suscripcion));
                auditLogger.success("UPSERT_SUBSCRIPTION", AuditMessages.SUBSCRIPTION_UPDATED,
                        "userId: " + dto.userId());
                return response;
            }

            Subscription nueva = new Subscription(dto.userId(), dto.deviceToken(), dto.productPreferences(), dto.heladeraIds());
            SubscriptionResponseDTO response = SubscriptionResponseDTO.from(repository.save(nueva));
            auditLogger.success("UPSERT_SUBSCRIPTION", AuditMessages.SUBSCRIPTION_CREATED,
                    "userId: " + dto.userId());
            return response;
        } catch (Exception e) {
            auditLogger.error("UPSERT_SUBSCRIPTION", "Error al crear/actualizar suscripción", e.getMessage());
            throw e;
        }
    }

    @Override
    public SubscriptionResponseDTO actualizar(Long userId, SubscriptionUpdateDTO dto) {
        try {
            Subscription suscripcion = repository.findByUserId(userId)
                    .orElseThrow(() -> {
                        auditLogger.error("UPDATE_SUBSCRIPTION", AuditMessages.SUBSCRIPTION_NOT_FOUND,
                                "userId: " + userId);
                        return new SubscriptionNotFoundException(
                                "Suscripción no encontrada para userId: " + userId);
                    });

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

            SubscriptionResponseDTO response = SubscriptionResponseDTO.from(repository.save(suscripcion));
            auditLogger.success("UPDATE_SUBSCRIPTION", AuditMessages.SUBSCRIPTION_UPDATED,
                    "userId: " + userId);
            return response;
        } catch (SubscriptionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            auditLogger.error("UPDATE_SUBSCRIPTION", "Error al actualizar suscripción", e.getMessage());
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
                    .orElseThrow(() -> {
                        auditLogger.error("DELETE_SUBSCRIPTION", AuditMessages.SUBSCRIPTION_NOT_FOUND,
                                "userId: " + userId);
                        return new SubscriptionNotFoundException(
                                "Suscripción no encontrada para userId: " + userId);
                    });
            repository.delete(suscripcion);
            auditLogger.success("DELETE_SUBSCRIPTION", AuditMessages.SUBSCRIPTION_DELETED,
                    "userId: " + userId);
        } catch (SubscriptionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            auditLogger.error("DELETE_SUBSCRIPTION", "Error al eliminar suscripción", e.getMessage());
            throw e;
        }
    }
}
