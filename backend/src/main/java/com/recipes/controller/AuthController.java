package com.recipes.controller;

import com.recipes.dto.*;
import com.recipes.service.AuthService;
import com.recipes.service.SmsService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final SmsService smsService;

    public AuthController(AuthService authService, SmsService smsService) {
        this.authService = authService;
        this.smsService = smsService;
    }

    @PostMapping("/register")
    public Result<UserDTO> register(@RequestBody RegisterRequest request) {
        try {
            UserDTO user = authService.register(request);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(401, e.getMessage());
        }
    }

    @GetMapping("/me")
    public Result<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        UserDTO user = authService.getUserById(userId);
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<UserDTO> updateProfile(@RequestBody UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        try {
            UserDTO user = authService.updateProfile(userId, userDTO);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/send-sms")
    public Result<Void> sendSmsCode(@RequestBody SmsRequest request) {
        try {
            smsService.sendSmsCode(request.getPhone());
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/verify-sms")
    public Result<Void> verifySmsCode(@RequestBody SmsCodeRequest request) {
        try {
            smsService.verifySmsCode(request.getPhone(), request.getCode());
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}
