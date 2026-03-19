package com.recipes.dto;

import lombok.Data;

@Data
public class SmsCodeRequest {
    private String phone;
    private String code;
}
