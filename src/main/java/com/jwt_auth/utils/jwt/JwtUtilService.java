package com.jwt_auth.utils.jwt;

import com.jwt_auth.utils.enums.StatusCodeEnum;
import com.jwt_auth.utils.exceptions.ApiException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

import static com.jwt_auth.utils.enums.StatusCodeEnum.*;

@Component
public class JwtUtilService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtilService.class);

    @Value("${jwt.secret.key}")
    private String secretKey;

    private SecretKey getSigningKey() {
        LOGGER.info("Getting signing key from JwtUtilService.getSigningKey");
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            LOGGER.info("Error getting signing key in JwtUtilService.getSigningKey: {}", e.getMessage());
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    INVALID_JWT_SECRET_KEY,
                    INVALID_JWT_SECRET_KEY.getMessage(),
                    LOGGER
            );
        }
    }

    public String generateJwtToken(String email) {
        LOGGER.info("Generating JWT token for email: {}", email);
        try {
            return Jwts.builder()
                    .subject(email)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            LOGGER.info("Error generating JWT token in JwtUtilService.generateJwtToken: {}", e.getMessage());
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    JWT_GENERATION_FAILED,
                    JWT_GENERATION_FAILED.getMessage(),
                    LOGGER
            );
        }
    }

    public String extractEmailFromToken(String token) {
        LOGGER.info("Extracting email from JWT token: {}", token);
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            LOGGER.info("Error occurred while extracting email from token in JwtUtilService.extractEmailFromToken: {}", e.getMessage());
            throw new ApiException(
                    HttpStatus.UNAUTHORIZED.value(),
                    JWT_EXTRACTION_FAILED,
                    JWT_EXTRACTION_FAILED.getMessage(),
                    LOGGER
            );
        }
    }

    public StatusCodeEnum validateJwtToken(String token) {
        LOGGER.info("Validating JWT token: {}", token);
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return VALID_JWT_TOKEN;
        } catch (ExpiredJwtException e) {
            LOGGER.warn("JWT token has expired: {}", e.getMessage());
            return JWT_TOKEN_EXPIRED;
        } catch (MalformedJwtException | SignatureException e) {
            LOGGER.warn("Invalid JWT token: {}", e.getMessage());
            return INVALID_JWT_TOKEN;
        } catch (Exception e) {
            LOGGER.error("JWT token validation failed: {}", e.getMessage(), e);
            return INVALID_JWT_TOKEN;
        }
    }
}
