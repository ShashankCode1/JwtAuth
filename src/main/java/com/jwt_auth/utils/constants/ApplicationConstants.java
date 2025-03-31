package com.jwt_auth.utils.constants;

public class ApplicationConstants {

    public static final String VALIDATION_FAILED = "Validation Failed";
    public static final String MALFORMED_REQUEST = "Malformed Request";
    public static final String INVALID_AUTH_TOKEN = "Invalid Auth Token";
    public static final String MALFORMED_AUTH_TOKEN = "Malformed Auth Token";
    public static final String INVALID_AUTH_SIGNATURE = "Invalid Auth Signature";
    public static final String EXPIRED_AUTH_TOKEN = "Expired Auth Token";
    public static final String RESPONSE = "response";
    public static final String JWT_TOKEN = "jwtToken";
    public static final String AUTHORIZATION = "Authorization";
    public static final String USER = "user";

    // Private constructor to prevent instantiation
    private ApplicationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
