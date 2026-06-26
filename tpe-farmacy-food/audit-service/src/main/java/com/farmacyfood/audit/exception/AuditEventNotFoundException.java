package com.farmacyfood.audit.exception;

public class AuditEventNotFoundException extends RuntimeException {

    public AuditEventNotFoundException(Long id) {
        super("Evento de auditoría no encontrado con id: " + id);
    }
}
