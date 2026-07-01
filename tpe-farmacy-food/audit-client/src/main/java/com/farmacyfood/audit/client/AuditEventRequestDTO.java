package com.farmacyfood.audit.client;

/**
 * DTO unificado para enviar eventos al audit-service.
 *
 * @param serviceName Nombre del servicio emisor (ej: "ORDER-SERVICE", "AUTH-SERVICE")
 * @param eventType   Tipo de evento: SUCCESS, ERROR, PENDING o INFO
 * @param action      Acción concreta (ej: "CREATE_ORDER", "PAY_ORDER")
 * @param message     Mensaje legible descriptivo del evento
 * @param detail      JSON string con detalles adicionales del evento (opcional)
 */
public record AuditEventRequestDTO(
        String serviceName,
        String eventType,
        String action,
        String message,
        String detail
) {}
