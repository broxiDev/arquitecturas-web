package com.farmacyfood.kitchen.exception;

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

        @Schema(description = "Mensaje descriptivo del error", example = "No existe plan para la fecha: 2026-06-15")
        String message,

        @Schema(description = "Timestamp del error", example = "2026-06-14T12:00:00")
        String timestamp
    ) {}

    @ExceptionHandler(PlanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlanNotFound(PlanNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
            "Not Found",
            ex.getMessage(),
            LocalDateTime.now().toString()
        ));
    }

    @ExceptionHandler(PlanAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePlanAlreadyExists(PlanAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
            "Conflict",
            ex.getMessage(),
            LocalDateTime.now().toString()
        ));
    }

    @ExceptionHandler(CatalogoException.class)
    public ResponseEntity<ErrorResponse> handleCatalogoError(CatalogoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
            "Error en el catálogo",
            ex.getMessage(),
            LocalDateTime.now().toString()
        ));
    }
}
