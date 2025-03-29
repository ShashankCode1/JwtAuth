package com.jwt_auth.utils.exceptions;

import com.jwt_auth.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.jwt_auth.utils.constants.ApplicationConstants.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle Validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                VALIDATION_FAILED,
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Handle Invalid JSON request errors
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleInvalidJsonRequest(
            HttpMessageNotReadableException ex) {
        LOGGER.error("Invalid JSON request: {}", ex.getMessage());

        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                MALFORMED_REQUEST,
                Map.of(RESPONSE, "Invalid JSON format or incorrect data type")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Handle ApiException errors
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleApiException(ApiException ex) {
        return ResponseEntity
                .status(ex.getStatus()).body(new ApiResponse<>(
                        ex.getStatus(),
                        ex.getMessage(),
                        Map.of(RESPONSE, ex.getMessage())
                ));
    }
}
