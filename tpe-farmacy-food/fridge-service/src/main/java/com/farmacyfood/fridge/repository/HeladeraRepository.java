package com.farmacyfood.fridge.repository;

import com.farmacyfood.fridge.entity.Heladera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeladeraRepository extends JpaRepository<Heladera, Long> {

    List<Heladera> findByStatus(String status);

    List<Heladera> findByCocinaId(String cocinaId);

    @Query(value = """
        SELECT * FROM heladera
        WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(latitude))
        * cos(radians(longitude) - radians(:lng)) + sin(radians(:lat))
        * sin(radians(latitude)))) < :radius
        ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(latitude))
        * cos(radians(longitude) - radians(:lng)) + sin(radians(:lat))
        * sin(radians(latitude))))
        """, nativeQuery = true)
    List<Heladera> findNearby(@Param("lat") double lat, @Param("lng") double lng, @Param("radius") double radius);
}
