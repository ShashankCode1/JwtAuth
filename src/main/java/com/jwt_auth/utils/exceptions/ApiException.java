package com.jwt_auth.utils.exceptions;

import com.jwt_auth.utils.enums.StatusCodeEnum;
import lombok.Getter;
import org.slf4j.Logger;

@Getter
public class ApiException extends RuntimeException {

    private final int status;
    private final StatusCodeEnum statusCodeEnum;

    public ApiException(int status, StatusCodeEnum statusCodeEnum, String message, Logger logger) {
        super(message);
        this.status = status;
        this.statusCodeEnum = statusCodeEnum;
        logger.error("status: {}, [{}]: {}", status, statusCodeEnum.getStatusCode(), message);
    }
}
