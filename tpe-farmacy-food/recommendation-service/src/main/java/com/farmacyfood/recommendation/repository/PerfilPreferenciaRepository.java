package com.farmacyfood.recommendation.repository;

import com.farmacyfood.recommendation.entity.PerfilPreferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilPreferenciaRepository extends JpaRepository<PerfilPreferencia, Long> {

    Optional<PerfilPreferencia> findByUserId(Long userId);
}
