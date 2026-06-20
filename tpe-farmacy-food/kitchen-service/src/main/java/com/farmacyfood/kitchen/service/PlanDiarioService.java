package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.dto.PlanDiarioResponseDTO;

import java.time.LocalDate;

public interface PlanDiarioService {
    // Obtiene el plan diario para una fecha y cocina específica
    PlanDiarioResponseDTO getPlanByDate(LocalDate date, String cocinaId);

    // Genera el plan diario para una fecha y cocina específica
    PlanDiarioResponseDTO generarPlan(LocalDate date, String cocinaId);
}