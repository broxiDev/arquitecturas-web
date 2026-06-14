package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.dto.PlanDiarioResponseDTO;

import java.time.LocalDate;

public interface PlanDiarioService {
    PlanDiarioResponseDTO getPlanByDate(LocalDate date);
    PlanDiarioResponseDTO generarPlan(LocalDate date);
}
