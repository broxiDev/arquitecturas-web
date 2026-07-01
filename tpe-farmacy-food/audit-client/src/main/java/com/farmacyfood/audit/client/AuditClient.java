package com.farmacyfood.audit.client;

/**
 * Contrato del cliente de auditoría.
 * Todas las implementaciones (Feign real, mock para dev) deben cumplir esta interfaz.
 */
public interface AuditClient {

    /**
     * Registra un evento de auditoría de forma fire-and-forget.
     *
     * @param request DTO con los datos del evento
     */
    void registrarEvento(AuditEventRequestDTO request);
}
