package com.jwt_auth.utils.exceptions;

import com.jwt_auth.model.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
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

import static com.jwt_auth.utils.constants.ApplicationConstants.RESPONSE;
import static com.jwt_auth.utils.enums.StatusCodeEnum.*;

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
                PAYLOAD_VALIDATION_FAILED,
                PAYLOAD_VALIDATION_FAILED.getMessage(),
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
                MALFORMED_REQUEST.getMessage(),
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
                        ex.getStatusCodeEnum(),
                        ex.getStatusCodeEnum().getMessage(),
                        Map.of(RESPONSE, ex.getMessage())
                ));
    }

    // Handle JwtException errors
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleJwtException(JwtException ex) {
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                INVALID_JWT_TOKEN,
                INVALID_JWT_TOKEN.getMessage(),
                Map.of(RESPONSE, ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Handle Malformed Jwt Token errors
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMalformedJwtException(MalformedJwtException ex) {
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                MALFORMED_JWT_TOKEN,
                MALFORMED_JWT_TOKEN.getMessage(),
                Map.of(RESPONSE, ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Handle Jwt Signature errors
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleSignatureException(SignatureException ex) {
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                INVALID_JWT_SIGNATURE,
                INVALID_JWT_SIGNATURE.getMessage(),
                Map.of(RESPONSE, ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Handle Jwt Token Expiration errors
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleExpiredJwtException(ExpiredJwtException ex) {
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                JWT_TOKEN_EXPIRED,
                JWT_TOKEN_EXPIRED.getMessage(),
                Map.of(RESPONSE, ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
