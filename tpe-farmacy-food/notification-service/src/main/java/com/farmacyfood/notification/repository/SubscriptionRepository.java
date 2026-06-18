package com.farmacyfood.notification.repository;

import com.farmacyfood.notification.entity.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

    Optional<Subscription> findByUserId(Long userId);

    List<Subscription> findByProductPreferencesContaining(Long productId);
}
