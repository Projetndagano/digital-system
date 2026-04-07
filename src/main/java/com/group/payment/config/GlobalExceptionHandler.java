package com.group.payment.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
public ResponseEntity<ApiResponse> handleRuntime(RuntimeException ex) {
    String msg = ex.getMessage();
    int status = 400; // Default

    if (msg.contains("not found")) status = 404;
    else if (msg.contains("already")) status = 409;
    else if (msg.contains("deactivated")) status = 403;
    else if (msg.contains("Invalid email or password")) status = 401;
    else if (msg.contains("Unauthorized") || msg.contains("Access is denied")) status = 401; // ADD THIS LINE

    return ResponseEntity.status(status).body(ApiResponse.error(msg));
}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return ResponseEntity.status(422).body(Map.of(
                "success", false,
                "message", "Validation failed",
                "errors", errors
        ));
    }
}