package com.recipes.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;
    private String password;
    private String smsCode;
}
