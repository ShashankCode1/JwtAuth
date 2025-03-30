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
    ;

    private final String statusCode;
    private final String message;
}
