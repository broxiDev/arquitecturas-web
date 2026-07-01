package com.farmacyfood.kitchen.repository;

import com.farmacyfood.kitchen.entity.postgres.Cocina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CocinaRepository extends JpaRepository<Cocina, Long> {

    boolean existsByUsuario(String usuario);

    Optional<Cocina> findById(Long id);

    Optional<Cocina> findByUsuario(String usuario);
}
