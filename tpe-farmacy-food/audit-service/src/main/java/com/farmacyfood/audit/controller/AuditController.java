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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auditoria")
@Tag(name = "Audit Service", description = "Registro de eventos de auditoría (fire-and-forget)")
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
}
