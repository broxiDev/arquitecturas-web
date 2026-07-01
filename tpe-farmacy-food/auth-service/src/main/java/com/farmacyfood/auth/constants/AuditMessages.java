package com.farmacyfood.auth.constants;

/**
 * Mensajes descriptivos para eventos de auditoría del servicio de autenticación.
 */
public final class AuditMessages {

    private AuditMessages() {}

    public static final String USER_REGISTERED = "Usuario registrado exitosamente";
    public static final String USER_LOGGED_IN = "Inicio de sesión exitoso";
    public static final String LOGIN_FAILED = "Credenciales inválidas";
    public static final String USER_ALREADY_EXISTS = "El usuario ya existe";
    public static final String INVALID_ROLE = "Rol no permitido";
}
