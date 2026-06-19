package com.farmacyfood.recommendation.controller;

import com.farmacyfood.recommendation.dto.RecomendacionResponseDTO;
import com.farmacyfood.recommendation.service.RecomendacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recomendaciones")
@RequiredArgsConstructor
@Tag(name = "Recomendaciones", description = "Sugerencias personalizadas de productos")
public class RecomendacionController {

    private final RecomendacionService recomendacionService;

    @GetMapping("/{userId}")
    @Operation(
            summary = "Obtener recomendaciones para un usuario",
            description = "Retorna una lista de productos recomendados basados en las preferencias dietarias e historial de compras del usuario. Ejemplo: userId=1 (VEGANO, SIN_GLUTEN) o userId=2 (VEGETARIANO)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recomendaciones obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<RecomendacionResponseDTO> getRecomendaciones(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long userId) {

        RecomendacionResponseDTO recomendaciones = recomendacionService.getRecomendaciones(userId);
        return ResponseEntity.ok(recomendaciones);
    }
}
