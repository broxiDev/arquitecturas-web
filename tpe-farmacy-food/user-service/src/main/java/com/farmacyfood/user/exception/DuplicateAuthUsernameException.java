package com.farmacyfood.user.exception;

public class DuplicateAuthUsernameException extends RuntimeException {
    public DuplicateAuthUsernameException(String message) {
        super(message);
    }
}
