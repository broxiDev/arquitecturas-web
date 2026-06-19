package com.farmacyfood.recommendation.exception;

public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(Long userId) {
        super("No se encontró el usuario con ID: " + userId);
    }
}
