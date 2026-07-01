package com.farmacyfood.kitchen.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cocina")
@Tag(name = "Kitchen Service", description = "Operaciones de salud del servicio de cocina fantasma")
public class HealthController {

    @Operation(summary = "Health check del servicio")
    @GetMapping("/health")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "kitchen-service"));
    }
}
