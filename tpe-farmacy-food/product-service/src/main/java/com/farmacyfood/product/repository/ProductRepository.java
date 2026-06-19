package com.farmacyfood.product.repository;

import com.farmacyfood.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDietaryCategory(String dietaryCategory);

    @Query("SELECT p FROM Product p WHERE p.catalogo.cocinaId = :cocinaId")
    List<Product> findByCocinaId(@Param("cocinaId") String cocinaId);

    @Query("SELECT p FROM Product p WHERE p.catalogo.cocinaId = :cocinaId AND p.name = :name")
    Optional<Product> findByCocinaIdAndName(@Param("cocinaId") String cocinaId, @Param("name") String name);
}
