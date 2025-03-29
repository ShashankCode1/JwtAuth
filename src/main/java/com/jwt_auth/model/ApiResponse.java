package com.jwt_auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;
}
