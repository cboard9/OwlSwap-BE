package com.cboard.owlswap.owlswap_backend.exception_handler;

import com.cboard.owlswap.owlswap_backend.exception.*;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


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

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ApiError> handleAccessDenied(Exception ex,
                                                       HttpServletRequest req) {

        ApiError body = new ApiError(
                "FORBIDDEN",
                "You do not have permission to perform this action.",
                HttpStatus.FORBIDDEN.value(),
                req.getRequestURI(),
                Instant.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);

    }

    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<?> handleEmailDeliveryException(EmailDeliveryException ex, HttpServletRequest req)
    {
        ApiError body = new ApiError(
                "EMAIL_DELIVERY_ERROR",
                "Server failed to deliver verification email.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                req.getRequestURI(),
                Instant.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<ApiError> handleStripeException(StripeException ex, HttpServletRequest req) {
        ApiError body = new ApiError(
                "STRIPE_ERROR",
                "Stripe request failed: " + ex.getMessage(),
                HttpStatus.BAD_GATEWAY.value(),
                req.getRequestURI(),
                Instant.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }




    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception on path {}: ", req.getRequestURI(), ex);

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
}

