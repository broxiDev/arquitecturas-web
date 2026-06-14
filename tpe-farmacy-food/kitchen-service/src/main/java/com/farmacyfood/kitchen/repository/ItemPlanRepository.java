package com.farmacyfood.kitchen.repository;

import com.farmacyfood.kitchen.entity.postgres.PlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPlanRepository extends JpaRepository<PlanItem, Long> {
    List<PlanItem> findByDailyPlanId(Long dailyPlanId);
}
