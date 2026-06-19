package com.farmacyfood.recommendation.repository;

import com.farmacyfood.recommendation.entity.HistorialComprasCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistorialComprasCacheRepository extends JpaRepository<HistorialComprasCache, Long> {

    Optional<HistorialComprasCache> findByUserId(Long userId);
}
