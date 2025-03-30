package com.jwt_auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jwt_auth.dto.LoginDTO;
import com.jwt_auth.dto.RegisterDTO;
import com.jwt_auth.model.ApiResponse;
import com.jwt_auth.model.UserPOJO;
import com.jwt_auth.repository.UserRepository;
import com.jwt_auth.utils.exceptions.ApiException;
import com.jwt_auth.utils.jwt.JwtUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.jwt_auth.utils.constants.ApplicationConstants.JWT_TOKEN;
import static com.jwt_auth.utils.constants.ApplicationConstants.RESPONSE;
import static com.jwt_auth.utils.enums.StatusCodeEnum.*;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserRepository userRepository;
    private final JwtUtilService jwtUtilService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtilService jwtUtilService) {
        this.userRepository = userRepository;
        this.jwtUtilService = jwtUtilService;
    }

    @Override
    public ApiResponse<JsonNode> registerUser(RegisterDTO registerRequest) {
        LOGGER.info("Started UserServiceImpl.registerUser at: {}", System.currentTimeMillis());

        // Check if email is already registered
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ApiException(
                    HttpStatus.CONFLICT.value(),
                    EMAIL_ALREADY_EXISTS_ERROR,
                    EMAIL_ALREADY_EXISTS_ERROR.getMessage() + " : " + registerRequest.getEmail(),
                    LOGGER);
        }

        // Convert DTO to POJO and encrypt password
        UserPOJO newUser = new UserPOJO();
        newUser.setUsername(registerRequest.getEmail().substring(0, registerRequest.getEmail().indexOf('@')));
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Registering User
        ObjectNode response = objectMapper.createObjectNode();
        try {
            UserPOJO registeredUser = userRepository.save(newUser);
            response.putPOJO("user", registeredUser);
            response.put(RESPONSE, "User registered successfully");
        } catch (Exception e) {
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    USER_REGISTRATION_FAILED,
                    USER_REGISTRATION_FAILED.getMessage() + " : " + registerRequest,
                    LOGGER
            );
        }

        LOGGER.info("Final response for registerUser: {}", response);
        LOGGER.info("Ended UserServiceImpl.registerUser at: {}", System.currentTimeMillis());
        return new ApiResponse<>(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(), response);
    }

    @Override
    public ApiResponse<JsonNode> loginUser(LoginDTO loginRequest) {
        LOGGER.info("Started UserServiceImpl.loginUser at: {}", System.currentTimeMillis());

        // Find user by email
        UserPOJO user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.UNAUTHORIZED.value(),
                        EMAIL_NOT_FOUND,
                        EMAIL_NOT_FOUND.getMessage() + " : " + loginRequest.getEmail(),
                        LOGGER
                ));

        // Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ApiException(
                    HttpStatus.UNAUTHORIZED.value(),
                    PASSWORD_NOT_MATCHED,
                    PASSWORD_NOT_MATCHED.getMessage() + " : " + loginRequest.getPassword(),
                    LOGGER
            );
        }

        // Generate JWT Token
        String jwtToken = jwtUtilService.generateJwtToken(user);
        LOGGER.info("Generated JWT token: {}", jwtToken);

        ObjectNode response = objectMapper.createObjectNode();
        response.putPOJO("user", user);
        response.put(JWT_TOKEN, jwtToken);
        LOGGER.info("Final response for loginUser: {}", response);
        LOGGER.info("Ended UserServiceImpl.loginUser at: {}", System.currentTimeMillis());
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), response);
    }
}
