package com.farmacyfood.kitchen.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PlanDiarioResponseDTO(
    Long id,
    LocalDate date,
    List<ItemPlanDTO> items,
    LocalDateTime createdAt
) {}
