package com.jwt_auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.jwt_auth.dto.LoginDTO;
import com.jwt_auth.dto.RegisterDTO;
import com.jwt_auth.model.ApiResponse;
import org.springframework.stereotype.Component;

@Component
public interface UserService {

    ApiResponse<JsonNode> registerUser(RegisterDTO registerRequest);

    ApiResponse<JsonNode> loginUser(LoginDTO loginRequest);

}
