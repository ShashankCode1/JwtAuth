package com.jwt_auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.jwt_auth.dto.UserDTO;
import com.jwt_auth.model.ApiResponse;
import org.springframework.stereotype.Component;

@Component
public interface UserService {

    ApiResponse<JsonNode> registerUser(UserDTO userRequest);

}
