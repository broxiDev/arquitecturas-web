package com.farmacyfood.kitchen.repository;

import com.farmacyfood.kitchen.entity.postgres.DailyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PlanDiarioRepository extends JpaRepository<DailyPlan, Long> {
    Optional<DailyPlan> findByDate(LocalDate date);
}
