package com.jwt_auth.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt_auth.model.ApiResponse;
import com.jwt_auth.utils.enums.StatusCodeEnum;
import com.jwt_auth.utils.exceptions.ApiException;
import com.jwt_auth.utils.jwt.JwtUtilService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import static com.jwt_auth.utils.constants.ApplicationConstants.*;
import static com.jwt_auth.utils.enums.StatusCodeEnum.INVALID_JWT_TOKEN;
import static com.jwt_auth.utils.enums.StatusCodeEnum.JWT_TOKEN_EXPIRED;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtilService jwtUtilService;
    private final ObjectMapper objectMapper;

    @Autowired
    public JwtAuthFilter(JwtUtilService jwtUtilService, ObjectMapper objectMapper) {
        this.jwtUtilService = jwtUtilService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        LOGGER.info("Received Authorization header: {}", authHeader);

        // Request validation check
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // Token Validation check
        StatusCodeEnum statusCodeEnum = jwtUtilService.validateJwtToken(token);
        switch (statusCodeEnum) {
            case JWT_TOKEN_EXPIRED:
                LOGGER.info("JWT token has expired: {}", token);
                ApiResponse<JsonNode> expiredJwtTokenResponse = new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        EXPIRED_AUTH_TOKEN,
                        objectMapper.createObjectNode().put(RESPONSE, JWT_TOKEN_EXPIRED.getMessage())
                );
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(objectMapper.writeValueAsString(expiredJwtTokenResponse));
                return;
            case VALID_JWT_TOKEN:
                LOGGER.info("Valid JWT token: {}", token);
                break;
            default:
                LOGGER.info("Invalid JWT token: {}", token);
                ApiResponse<JsonNode> invalidJwtTokenResponse = new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        INVALID_AUTH_TOKEN,
                        objectMapper.createObjectNode().put(RESPONSE, INVALID_JWT_TOKEN.getMessage())
                );
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(objectMapper.writeValueAsString(invalidJwtTokenResponse));
                return;
        }

        try {
            String email = jwtUtilService.extractEmailFromToken(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            LOGGER.info("User authenticated successfully: {}", email);
        } catch (JwtException e) {
            LOGGER.warn("Invalid JWT token from JwtAuthFilter.doFilterInternal: {}", e.getMessage());
            throw new ApiException(
                    HttpStatus.UNAUTHORIZED.value(),
                    INVALID_JWT_TOKEN,
                    INVALID_JWT_TOKEN.getMessage(),
                    LOGGER
            );
        }

        filterChain.doFilter(request, response);
    }
}
