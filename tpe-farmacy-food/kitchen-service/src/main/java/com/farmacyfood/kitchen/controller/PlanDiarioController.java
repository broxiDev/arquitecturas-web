package com.farmacyfood.kitchen.controller;

import com.farmacyfood.kitchen.dto.PlanDiarioResponseDTO;
import com.farmacyfood.kitchen.service.PlanDiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/cocina/plan-diario")
@RequiredArgsConstructor
@Tag(name = "Plan Diario", description = "Planificación diaria de producción")
public class PlanDiarioController {

    private final PlanDiarioService planDiarioService;

    @Operation(summary = "Obtener plan diario")
    @GetMapping
    public ResponseEntity<PlanDiarioResponseDTO> getPlan(
            @Parameter(description = "Fecha del plan (YYYY-MM-DD). Si no se envía, usa la fecha actual.")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate targetDate = fecha != null ? fecha : LocalDate.now();
        PlanDiarioResponseDTO plan = planDiarioService.getPlanByDate(targetDate);
        return ResponseEntity.ok(plan);
    }

    @Operation(summary = "Generar o actualizar plan diario")
    @PostMapping
    public ResponseEntity<PlanDiarioResponseDTO> generarPlan(
            @Parameter(description = "Fecha del plan (YYYY-MM-DD). Si no se envía, usa la fecha actual.")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate targetDate = fecha != null ? fecha : LocalDate.now();
        PlanDiarioResponseDTO plan = planDiarioService.generarPlan(targetDate);
        return ResponseEntity.ok(plan);
    }
}
