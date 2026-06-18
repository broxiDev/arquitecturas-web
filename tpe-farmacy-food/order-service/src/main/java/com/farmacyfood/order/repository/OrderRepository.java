package com.farmacyfood.order.repository;

import com.farmacyfood.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    //SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

}
