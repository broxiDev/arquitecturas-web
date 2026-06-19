package com.farmacyfood.product.repository;

import com.farmacyfood.product.entity.Catalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {
    Optional<Catalogo> findByCocinaId(String cocinaId);
    boolean existsByCocinaId(String cocinaId);
}
