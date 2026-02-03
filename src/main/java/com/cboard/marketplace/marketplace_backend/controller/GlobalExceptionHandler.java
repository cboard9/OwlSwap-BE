package com.cboard.marketplace.marketplace_backend.controller;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler
{

    public record ApiError(
            String code,
            String message,
            Instant timestamp
    ) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        // Get the most specific (SQL) message, e.g. "Incorrect date value: '8/25/2025' for column 'deadline'..."
        String root = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();

        // Optional: make a friendlier message if it looks like a MySQL date format error
        String message = root != null && root.contains("Incorrect date value")
                ? "Invalid date format for 'deadline'. Use YYYY-MM-DD."
                : "Request violates a database constraint: " + root;

        ApiError body = new ApiError(
                "DATA_INTEGRITY_VIOLATION",
                message,
                Instant.now()
        );

        // 400 since this is a bad client payload, not a server failure
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}

