package com.farmacyfood.kitchen.controller;

import com.farmacyfood.kitchen.dto.ItemPlanDTO;
import com.farmacyfood.kitchen.dto.PlanDiarioResponseDTO;
import com.farmacyfood.kitchen.service.PlanDiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cocina/plan-diario")
@RequiredArgsConstructor
@Tag(name = "Plan Diario", description = "Planificación diaria de producción")
public class PlanDiarioController {

    private final PlanDiarioService planDiarioService;

    @Operation(
        summary = "Obtener plan diario",
        description = "Retorna el plan de producción para una fecha y cocina específica. Si no se envía fecha, usa el día actual. Devuelve 404 si no existe plan para esa fecha y cocina."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Plan encontrado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PlanDiarioResponseDTO.class),
                examples = @ExampleObject(
                    name = "Plan diario",
                    value = """
                    {
                      "id": 1,
                      "date": "2026-06-14",
                      "cocinaId": "COCINA-DULCE",
                      "items": [
                        {"productId": 101, "productName": "Brownie de Chocolate", "suggestedQuantity": 10},
                        {"productId": 102, "productName": "Cheesecake", "suggestedQuantity": 6},
                        {"productId": 103, "productName": "Tiramisú", "suggestedQuantity": 8}
                      ],
                      "createdAt": "2026-06-14T08:00:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No existe plan para la fecha y cocina solicitada",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Plan no encontrado",
                    value = """
                    {
                      "error": "Not Found",
                      "message": "No existe plan para la fecha: 2026-06-15 y cocina: COCINA-DULCE",
                      "timestamp": "2026-06-14T12:00:00"
                    }
                    """
                )
            )
        )
    })
    @GetMapping
    public ResponseEntity<PlanDiarioResponseDTO> getPlan(
            @Parameter(description = "ID de la cocina fantasma", example = "COCINA-DULCE")
            @RequestParam @NotBlank String cocinaId,
            @Parameter(description = "Fecha del plan (YYYY-MM-DD). Si no se envía, usa la fecha actual.", example = "2026-06-14")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate targetDate = fecha != null ? fecha : LocalDate.now();
        PlanDiarioResponseDTO plan = planDiarioService.getPlanByDate(targetDate, cocinaId);
        return ResponseEntity.ok(plan);
    }

    @Operation(
        summary = "Generar plan diario",
        description = "Calcula el plan de producción basado en ventas históricas (últimos 7 días) y stock remanente en heladeras, y lo guarda. Si ya existe un plan para esa fecha y cocina, retorna 409."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Plan generado exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PlanDiarioResponseDTO.class),
                examples = @ExampleObject(
                    name = "Plan generado",
                    value = """
                    {
                      "id": 1,
                      "date": "2026-06-14",
                      "cocinaId": "COCINA-DULCE",
                      "items": [
                        {"productId": 101, "productName": "Brownie de Chocolate", "suggestedQuantity": 7},
                        {"productId": 102, "productName": "Cheesecake", "suggestedQuantity": 4}
                      ],
                      "createdAt": "2026-06-14T08:00:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Ya existe un plan para esa fecha y cocina",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Plan ya existe",
                    value = """
                    {
                      "error": "Conflict",
                      "message": "Ya existe un plan para la fecha: 2026-06-14 y cocina: COCINA-DULCE",
                      "timestamp": "2026-06-14T12:00:00"
                    }
                    """
                )
            )
        )
    })
    @PostMapping
    public ResponseEntity<PlanDiarioResponseDTO> generarPlan(
            @Parameter(description = "ID de la cocina fantasma", example = "COCINA-DULCE")
            @RequestParam @NotBlank String cocinaId,
            @Parameter(description = "Fecha del plan (YYYY-MM-DD). Si no se envía, usa la fecha actual.", example = "2026-06-14")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate targetDate = fecha != null ? fecha : LocalDate.now();
        PlanDiarioResponseDTO plan = planDiarioService.generarPlan(targetDate, cocinaId);
        return ResponseEntity.ok(plan);
    }
}