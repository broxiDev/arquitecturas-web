package com.farmacyfood.notification.repository;

import com.farmacyfood.notification.entity.Suscripcion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuscripcionRepository extends MongoRepository<Suscripcion, String> {

    Optional<Suscripcion> findByUserId(Long userId);

    List<Suscripcion> findByProductPreferencesContaining(Long productId);
}
