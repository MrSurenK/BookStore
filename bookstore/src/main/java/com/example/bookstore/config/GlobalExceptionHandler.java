package com.example.bookstore.config;

import com.example.bookstore.customExceptions.BadRequestException;
import com.example.bookstore.customExceptions.BookNotFoundException;
import com.example.bookstore.dto.Res;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------------------------
    // 404 - Entity not found
    // ---------------------------
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Res<?>> handleEntityNotFound(EntityNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Res.error("Resource not found", ex.getMessage())
        );
    }

    // ---------------------------
    // 404 - Domain-specific (recommended)
    // ---------------------------
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Res<?>> handleBookNotFound(BookNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Res.error("Book not found", ex.getMessage())
        );
    }

    // ---------------------------
    // 400 - Validation errors
    // ---------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Res<?>> handleValidation(MethodArgumentNotValidException ex) {

        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Res.error("Validation failed", errorMsg)
        );
    }

    // ---------------------------
    // 400 - Bad request (custom)
    // ---------------------------
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Res<?>> handleBadRequest(BadRequestException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Res.error("Bad request", ex.getMessage())
        );
    }

    // ---------------------------
    // 500 - fallback
    // ---------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Res<?>> handleGeneric(Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Res.error("Internal server error", ex.getMessage())
        );
    }
}