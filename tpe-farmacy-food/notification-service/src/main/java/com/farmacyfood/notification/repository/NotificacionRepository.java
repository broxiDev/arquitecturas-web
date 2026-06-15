package com.farmacyfood.notification.repository;

import com.farmacyfood.notification.entity.Notificacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends MongoRepository<Notificacion, String> {

    List<Notificacion> findByUserIdOrderBySentAtDesc(Long userId);

    List<Notificacion> findByUserIdAndReadOrderBySentAtDesc(Long userId, Boolean read);
}
