package com.farmacyfood.kitchen.repository;

import com.farmacyfood.kitchen.entity.mongo.VentaHistorica;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaHistoricaRepository extends MongoRepository<VentaHistorica, String> {

    @Query("{'date': {$gte: ?0, $lte: ?1}}")
    List<VentaHistorica> findByDateRange(LocalDate from, LocalDate to);

    List<VentaHistorica> findByProductId(Long productId);

    List<VentaHistorica> findByFridgeId(Long fridgeId);

    @Query("{'productId': ?0, 'date': {$gte: ?1, $lte: ?2}}")
    List<VentaHistorica> findByProductIdAndDateRange(Long productId, LocalDate from, LocalDate to);

    @Query("{'fridgeId': ?0, 'date': {$gte: ?1, $lte: ?2}}")
    List<VentaHistorica> findByFridgeIdAndDateRange(Long fridgeId, LocalDate from, LocalDate to);
}
