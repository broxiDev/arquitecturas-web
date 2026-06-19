package com.farmacyfood.order.exception;

public class FailedPaymentException extends RuntimeException {
    public FailedPaymentException(String message) {
        super(message);
    }
}
