package com.jwt_auth.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCodeEnum {
    EMAIL_ALREADY_EXISTS_ERROR(
            "EMAIL_ALREADY_EXISTS_ERROR", "Exception occurred while registering user with already existing email"),
    USER_REGISTRATION_FAILED("USER_REGISTRATION_FAILED", "Exception occurred while registering user"),
    EMAIL_NOT_FOUND("EMAIL_NOT_FOUND", "Exception occurred while logging in user as email not registered"),
    PASSWORD_NOT_MATCHED("PASSWORD_NOT_MATCHED", "Exception occurred while logging in user as password not matched"),
    INVALID_JWT_TOKEN("INVALID_JWT_TOKEN", "Exception occurred while validating JWT token"),
    INVALID_JWT_SECRET_KEY("INVALID_JWT_SECRET_KEY", "Exception occurred while decoding secret key"),
    JWT_GENERATION_FAILED("JWT_GENERATION_FAILED", "Exception occurred while generating JWT token"),
    JWT_TOKEN_EXPIRED("JWT_TOKEN_EXPIRED", "Exception occurred as JWT token is expired"),
    JWT_EXTRACTION_FAILED("JWT_EXTRACTION_FAILED", "Exception occurred while extracting JWT token"),
    VALID_JWT_TOKEN("VALID_JWT_TOKEN", "Validated Jwt Token"),
    ;

    private final String statusCode;
    private final String message;
}
