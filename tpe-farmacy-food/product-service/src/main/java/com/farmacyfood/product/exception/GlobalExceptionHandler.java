package com.farmacyfood.product.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Schema(description = "Respuesta de error")
    public record ErrorResponse(
        @Schema(description = "Tipo de error", example = "Not Found")
        String error,

        @Schema(description = "Mensaje descriptivo del error", example = "Producto no encontrado")
        String message,

        @Schema(description = "Timestamp del error", example = "2026-06-15T12:00:00")
        String timestamp
    ) {}

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
            "Producto no encontrado",
            ex.getMessage(),
            LocalDateTime.now().toString()
        ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
            "Error en la solicitud",
            ex.getMessage(),
            LocalDateTime.now().toString()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
            "Error interno del servidor",
            "Ocurrió un error al procesar la solicitud:" + ex.getMessage(),
            LocalDateTime.now().toString()
        ));
    }
}
