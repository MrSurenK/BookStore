package com.example.bookstore.config;


import com.example.bookstore.dto.Res;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<Res<?>> handleNotFound(EntityNotFoundException ex){
        return ResponseEntity.status(404).body(
                Res.error("Resource not found", ex.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Res<?>> handleValidation(MethodArgumentNotValidException ex) {

        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> STR."\{err.getField()}: \{err.getDefaultMessage()}")
                .findFirst()
                .orElse("Validation error");

        return ResponseEntity.status(400).body(
                Res.error("Validation failed", errorMsg)
        );

    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Res<?>> handleBadRequest(BadRequestException ex) {

    return ResponseEntity.status(400).body(
        Res.error("Bad request", ex.getMessage())
    );
}


}
