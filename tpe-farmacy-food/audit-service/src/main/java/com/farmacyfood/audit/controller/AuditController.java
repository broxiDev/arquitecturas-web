package com.farmacyfood.audit.controller;

import com.farmacyfood.audit.dto.AuditEventFilterDTO;
import com.farmacyfood.audit.dto.AuditEventRequest;
import com.farmacyfood.audit.dto.AuditEventResponse;
import com.farmacyfood.audit.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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

    @Operation(summary = "Obtener un evento de auditoría por ID")
    @GetMapping("/eventos/{id}")
    public ResponseEntity<AuditEventResponse> obtenerEvento(
            @PathVariable Long id) {
        AuditEventResponse response = auditService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar eventos de auditoría con filtros opcionales y paginación")
    @GetMapping("/eventos")
    public ResponseEntity<Page<AuditEventResponse>> listarEventos(
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        AuditEventFilterDTO filtro = new AuditEventFilterDTO(
                serviceName, entityType, entityId, action, null, from, to, page, size);
        Page<AuditEventResponse> eventos = auditService.listarEventos(filtro);
        return ResponseEntity.ok(eventos);
    }

    @Operation(summary = "Obtener eventos de una entidad específica (por tipo e ID)")
    @GetMapping("/eventos/entidad/{entityType}/{entityId}")
    public ResponseEntity<List<AuditEventResponse>> obtenerPorEntidad(
            @PathVariable String entityType,
            @PathVariable String entityId) {
        List<AuditEventResponse> eventos = auditService.obtenerPorEntidad(entityType, entityId);
        return ResponseEntity.ok(eventos);
    }

    @Operation(summary = "Obtener eventos de un servicio específico")
    @GetMapping("/eventos/servicio/{serviceName}")
    public ResponseEntity<List<AuditEventResponse>> obtenerPorServicio(
            @PathVariable String serviceName) {
        List<AuditEventResponse> eventos = auditService.obtenerPorServicio(serviceName);
        return ResponseEntity.ok(eventos);
    }

    @Operation(summary = "Purgar eventos anteriores a una fecha (administrativo)")
    @DeleteMapping("/eventos/antiguos")
    public ResponseEntity<Void> purgarEventosAntiguos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
        auditService.purgarEventosAntiguos(before);
        return ResponseEntity.noContent().build();
    }
}
