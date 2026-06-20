package com.farmacyfood.fridge.exception;

public class CocinaNotFoundException extends RuntimeException {
    public CocinaNotFoundException(String message) {
        super(message);
    }
}
