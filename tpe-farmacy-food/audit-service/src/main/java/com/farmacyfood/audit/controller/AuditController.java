package com.farmacyfood.audit.controller;

import com.farmacyfood.audit.dto.AuditEventRequest;
import com.farmacyfood.audit.dto.AuditEventResponse;
import com.farmacyfood.audit.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auditoria")
@Tag(name = "Audit Service", description = "Registro y consulta de eventos de auditoría")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @Operation(summary = "Registrar un evento de auditoría")
    @PostMapping("/eventos")
    public ResponseEntity<AuditEventResponse> registrarEvento(
            @Valid @RequestBody AuditEventRequest request) {
        AuditEventResponse response = auditService.registrarEvento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar eventos de auditoría")
    @GetMapping("/eventos")
    public ResponseEntity<List<AuditEventResponse>> listarEventos(
            @RequestParam(required = false) String serviceName) {
        return ResponseEntity.ok(auditService.listarEventos(serviceName));
    }

    @Operation(summary = "Obtener un evento de auditoría por ID")
    @GetMapping("/eventos/{id}")
    public ResponseEntity<AuditEventResponse> obtenerEvento(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.obtenerEvento(id));
    }
}
