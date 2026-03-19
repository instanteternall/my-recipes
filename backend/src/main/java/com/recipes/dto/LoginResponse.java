package com.recipes.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private UserDTO user;
}
