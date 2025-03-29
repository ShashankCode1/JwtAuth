package com.jwt_auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jwt_auth.dto.UserDTO;
import com.jwt_auth.model.ApiResponse;
import com.jwt_auth.model.UserPOJO;
import com.jwt_auth.repository.UserRepository;
import com.jwt_auth.utils.exceptions.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.jwt_auth.utils.constants.ApplicationConstants.RESPONSE;
import static com.jwt_auth.utils.enums.StatusCodeEnum.EMAIL_ALREADY_EXISTS_ERROR;
import static com.jwt_auth.utils.enums.StatusCodeEnum.USER_REGISTRATION_FAILED;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponse<JsonNode> registerUser(UserDTO userRequest) {
        LOGGER.info("Started UserServiceImpl.registerUser at: {}", System.currentTimeMillis());

        // Check if email is already registered
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new ApiException(
                    HttpStatus.CONFLICT.value(),
                    EMAIL_ALREADY_EXISTS_ERROR,
                    EMAIL_ALREADY_EXISTS_ERROR.getMessage() + " : " + userRequest.getEmail(),
                    LOGGER);
        }

        // Convert DTO to POJO and encrypt password
        UserPOJO newUser = new UserPOJO();
        newUser.setUsername(userRequest.getEmail().substring(0, userRequest.getEmail().indexOf('@')));
        newUser.setEmail(userRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));

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
                    USER_REGISTRATION_FAILED.getMessage() + " : " + userRequest,
                    LOGGER
            );
        }

        LOGGER.info("Final response: {}", response);
        LOGGER.info("Ended UserServiceImpl.registerUser at: {}", System.currentTimeMillis());
        return new ApiResponse<>(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(), response);
    }
}
