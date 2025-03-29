package com.jwt_auth.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCodeEnum {
    EMAIL_ALREADY_EXISTS_ERROR(
            "EMAIL_ALREADY_EXISTS_ERROR", "Exception occurred while registering user with already existing email"),
    USER_REGISTRATION_FAILED("USER_REGISTRATION_FAILED", "Exception occurred while registering user"),
    ;

    private final String statusCode;
    private final String message;
}
