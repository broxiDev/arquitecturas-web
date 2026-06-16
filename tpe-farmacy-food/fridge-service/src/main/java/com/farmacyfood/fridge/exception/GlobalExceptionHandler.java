package com.farmacyfood.fridge.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Schema(description = "Respuesta de error")
    public record ErrorResponse(
        @Schema(description = "Tipo de error", example = "Not Found")
        String error,

        @Schema(description = "Mensaje descriptivo del error", example = "No existe heladera con id: 99")
        String message,

        @Schema(description = "Timestamp del error", example = "2026-06-14T12:00:00")
        String timestamp
    ) {}

    @ExceptionHandler(HeladeraNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHeladeraNotFound(HeladeraNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
            "Not Found", ex.getMessage(), LocalDateTime.now().toString()
        ));
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleStockInsuficiente(StockInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
            "Stock Insuficiente", ex.getMessage(), LocalDateTime.now().toString()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b)
            .orElse("Error de validación");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
            "Validation Error", message, LocalDateTime.now().toString()
        ));
    }
}
