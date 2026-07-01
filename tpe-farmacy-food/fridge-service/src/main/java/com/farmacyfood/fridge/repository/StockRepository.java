package com.farmacyfood.fridge.repository;

import com.farmacyfood.fridge.entity.StockHeladera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockHeladera, Long> {

    List<StockHeladera> findByHeladeraId(Long heladeraId);

    List<StockHeladera> findByUsername(String username);

    Optional<StockHeladera> findByHeladeraIdAndUsernameAndProductId(Long heladeraId, String username, Long productId);
}
