package com.cboard.owlswap.owlswap_backend.exception_handler;

import com.cboard.owlswap.owlswap_backend.exception.BadRequestException;
import com.cboard.owlswap.owlswap_backend.exception.DtoMappingException;
import com.cboard.owlswap.owlswap_backend.exception.NotAvailableException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
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

    /*    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }*/

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgNotValid(MethodArgumentNotValidException ex, HttpServletRequest req)
    {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.put(err.getField(), err.getDefaultMessage())
        );

        ApiError body = new ApiError(
            "VALIDATION_FAILED",
                "Request validation failed",
                HttpStatus.BAD_REQUEST.value(),
                req.getRequestURI(),
                Instant.now(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req)
    {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                fieldErrors.put(v.getPropertyPath().toString(), v.getMessage())
        );

        ApiError body = new ApiError(
                "VALIDATION_FAILED",
                "Request validation failed",
                HttpStatus.BAD_REQUEST.value(),
                req.getRequestURI(),
                Instant.now(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(body);
    }

/*    @ExceptionHandler(DataIntegrityViolationException.class)
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
    }*/

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        String root = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();

        String message = root != null && root.contains("Incorrect date value")
                ? "Invalid date format for 'deadline'. Use YYYY-MM-DD."
                : "Request violates a database constraint.";

        ApiError body = new ApiError(
                "DATA_INTEGRITY_VIOLATION",
                message,
                HttpStatus.BAD_REQUEST.value(),
                req.getRequestURI(),
                Instant.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        // In production you log ex with stacktrace; don’t leak internals to clients.
        ApiError body = new ApiError(
                "INTERNAL_ERROR",
                "An unexpected error occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                req.getRequestURI(),
                Instant.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }


    //SPECIFIC HANDLERS

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req)
    {
        ApiError body = new ApiError(
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                req.getRequestURI(),
                Instant.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(NotAvailableException.class)
    public ResponseEntity<ApiError> handleNotAvailable(NotAvailableException ex, HttpServletRequest req)
    {
        ApiError body = new ApiError(
                "ITEM_NOT_AVAILABLE",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                req.getRequestURI(),
                Instant.now(),
                null
        );

        //state conflicts with requested action (already sold)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest req)
    {
        ApiError body = new ApiError(
                "BAD_REQUEST",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                req.getRequestURI(),
                Instant.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DtoMappingException.class)
    public ResponseEntity<ApiError> handleDtoMapping(DtoMappingException ex, HttpServletRequest req)
    {
        ApiError body = new ApiError(
                "DTO_MAPPING_ERROR",
                //ex.getMessage(), // not logging here?
                "Server failed to serialize item data.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                req.getRequestURI(),
                Instant.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

