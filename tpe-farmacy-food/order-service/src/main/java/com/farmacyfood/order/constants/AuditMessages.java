package com.farmacyfood.order.constants;

/**
 * Mensajes descriptivos para eventos de auditoría del servicio de órdenes.
 */
public final class AuditMessages {

    private AuditMessages() {}

    // -- Acciones exitosas --
    public static final String ORDER_CREATED = "Orden creada exitosamente";
    public static final String ORDER_PAID = "Orden pagada exitosamente";
    public static final String PICKUP_CONFIRMED = "Retiro confirmado exitosamente";
    public static final String ORDER_CANCELLED = "Orden cancelada exitosamente";

    // -- Errores --
    public static final String ORDER_NOT_FOUND = "Orden no encontrada";
    public static final String INSUFFICIENT_STOCK = "Stock insuficiente para completar la orden";
    public static final String PRODUCT_NOT_FOUND = "Producto no encontrado en la heladera";
    public static final String PAYMENT_FAILED = "El pago no fue completado";
    public static final String ORDER_NOT_PENDING = "La orden no está en estado PENDING";
    public static final String ORDER_NOT_PAID = "La orden no está en estado PAID";
    public static final String ORDER_NOT_OWNER = "No puedes cancelar una orden de otro usuario";
    public static final String ORDER_INVALID_STATUS_CANCEL = "La orden no está en estado PENDING o PAID";
}
