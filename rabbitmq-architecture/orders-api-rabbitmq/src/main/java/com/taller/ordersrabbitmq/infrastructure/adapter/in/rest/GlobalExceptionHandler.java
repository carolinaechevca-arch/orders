package com.taller.ordersrabbitmq.infrastructure.adapter.in.rest;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        UUID correlationId = UUID.randomUUID();
        Map<String, String> validationErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> validationErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        log.warn("Solicitud rechazada por errores de validación. correlationId={}, path={}, validationErrors={}", correlationId, request.getRequestURI(), validationErrors);
        return build(HttpStatus.BAD_REQUEST, "La solicitud contiene errores de validación", request, correlationId, validationErrors);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        UUID correlationId = UUID.randomUUID();
        log.error("Error inesperado procesando la solicitud. correlationId={}, path={}", correlationId, request.getRequestURI(), exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "No fue posible procesar la solicitud", request, correlationId, Map.of());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, HttpServletRequest request, UUID correlationId, Map<String, String> validationErrors) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI(), correlationId, validationErrors);
        return ResponseEntity.status(status).body(response);
    }
}
