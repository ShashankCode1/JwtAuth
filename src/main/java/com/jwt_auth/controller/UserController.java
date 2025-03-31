package com.jwt_auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.jwt_auth.dto.LoginDTO;
import com.jwt_auth.dto.RegisterDTO;
import com.jwt_auth.model.ApiResponse;
import com.jwt_auth.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.jwt_auth.utils.constants.ApplicationConstants.AUTHORIZATION;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JsonNode>> registerUser(@Valid @RequestBody RegisterDTO registerRequest) {
        LOGGER.info("Started UserController.registerUser at: {}", System.currentTimeMillis());
        LOGGER.info("Received registerRequest: {}", registerRequest);
        ApiResponse<JsonNode> response = userService.registerUser(registerRequest);
        LOGGER.info("Ended UserController.registerUser at: {}", System.currentTimeMillis());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JsonNode>> loginUser(@Valid @RequestBody LoginDTO loginRequest) {
        LOGGER.info("Started UserController.loginUser at: {}", System.currentTimeMillis());
        LOGGER.info("Received loginRequest: {}", loginRequest);
        ApiResponse<JsonNode> response = userService.loginUser(loginRequest);
        LOGGER.info("Ended UserController.loginUser at: {}", System.currentTimeMillis());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<JsonNode>> getUserProfile(@RequestHeader(AUTHORIZATION) String token) {
        LOGGER.info("Started UserController.getUserProfile at: {}", System.currentTimeMillis());
        LOGGER.info("Received JWT token: {}", token);
        ApiResponse<JsonNode> response = userService.getUserProfile(token);
        LOGGER.info("Ended UserController.getUserProfile at: {}", System.currentTimeMillis());
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
