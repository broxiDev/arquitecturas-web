package com.farmacyfood.audit.client;

/**
 * Tipos de eventos de auditoría.
 * <ul>
 *   <li>{@link #SUCCESS} — Operación completada exitosamente</li>
 *   <li>{@link #ERROR} — Operación que falló</li>
 *   <li>{@link #PENDING} — Operación en proceso</li>
 *   <li>{@link #INFO} — Evento informativo (sin éxito ni error)</li>
 * </ul>
 */
public enum EventType {
    SUCCESS,
    ERROR,
    PENDING,
    INFO
}
