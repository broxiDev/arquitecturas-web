package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.dto.PlanDiarioResponseDTO;

import java.time.LocalDate;

public interface PlanDiarioService {
    // Obtiene el plan diario para una fecha y cocina especifico
    PlanDiarioResponseDTO getPlanByDate(LocalDate date, Long cocinaId);

    // Genera el plan diario para una fecha y cocina especifico
    PlanDiarioResponseDTO generarPlan(LocalDate date, Long cocinaId);
}
