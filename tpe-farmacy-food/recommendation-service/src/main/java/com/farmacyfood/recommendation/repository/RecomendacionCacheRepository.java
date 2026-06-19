package com.farmacyfood.recommendation.repository;

import com.farmacyfood.recommendation.entity.RecomendacionResultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecomendacionCacheRepository extends JpaRepository<RecomendacionResultado, Long> {

    Optional<RecomendacionResultado> findByUserId(Long userId);
}
