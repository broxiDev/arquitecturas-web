package com.farmacyfood.notification.constants;

/**
 * Mensajes descriptivos para eventos de auditoría del servicio de notificaciones.
 */
public final class AuditMessages {

    private AuditMessages() {}

    public static final String SUBSCRIPTION_CREATED = "Suscripción creada exitosamente";
    public static final String SUBSCRIPTION_UPDATED = "Suscripción actualizada exitosamente";
    public static final String SUBSCRIPTION_DELETED = "Suscripción eliminada exitosamente";
    public static final String AVAILABILITY_NOTIFIED = "Notificación de disponibilidad enviada";
    public static final String STATUS_CHANGE_NOTIFIED = "Cambio de estado de heladera notificado";
    public static final String NOTIFICATION_MARKED_READ = "Notificación marcada como leída";
    public static final String NOTIFICATION_DELETED = "Notificación eliminada";
    public static final String SUBSCRIPTION_NOT_FOUND = "Suscripción no encontrada";
    public static final String NOTIFICATION_NOT_FOUND = "Notificación no encontrada";
}
