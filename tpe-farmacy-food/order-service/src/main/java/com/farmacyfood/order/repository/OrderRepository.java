package com.farmacyfood.order.repository;

import com.farmacyfood.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    //SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    //busca órdenes con status IN ('PAID', 'PICKED_UP') cuya fecha esté entre from y to (que son opcionales).
    //Usa CAST(o.createdAt AS date) para comparar solo la fecha (sin hora). Si fridgeId no es null, filtra por heladera
    // Usa JOIN FETCH o.itemList para traer los items en la misma consulta y evitar LazyInitializationException.
    // Usa DISTINCT porque el JOIN FETCH puede duplicar órdenes (una por cada item).
    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.itemList " +
            "WHERE o.status IN ('PAID', 'PICKED_UP') " +
            "AND (:from IS NULL OR CAST(o.createdAt AS date) >= :from) " +
            "AND (:to IS NULL OR CAST(o.createdAt AS date) <= :to) " +
            "AND (:fridgeId IS NULL OR o.fridgeId = :fridgeId)")
    List<Order> findCompletedOrdersBetween(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("fridgeId") Long fridgeId);

}
