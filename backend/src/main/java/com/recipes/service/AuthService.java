package com.recipes.service;

import com.recipes.dto.*;
import com.recipes.entity.User;
import com.recipes.mapper.UserMapper;
import com.recipes.service.SmsService;
import com.recipes.utils.JwtUtil;
import com.recipes.utils.PhoneValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Service
public class AuthService {
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final SmsService smsService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserMapper userMapper, JwtUtil jwtUtil, SmsService smsService) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.smsService = smsService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public LoginResponse login(LoginRequest request) {
        if (!PhoneValidator.validate(request.getPhone())) {
            throw new RuntimeException("手机号格式错误");
        }
        // 验证短信验证码
        smsService.verifySmsCode(request.getPhone(), request.getSmsCode());
        
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", request.getPhone());
        User user = userMapper.selectOne(wrapper);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(convertToDTO(user));
        return response;
    }

    public UserDTO register(RegisterRequest request) {
        if (!PhoneValidator.validate(request.getPhone())) {
            throw new RuntimeException("手机号格式错误");
        }
        // 验证短信验证码
        smsService.verifySmsCode(request.getPhone(), request.getSmsCode());
        
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", request.getPhone());
        if (userMapper.selectOne(wrapper) != null) {
            throw new RuntimeException("手机号已被注册");
        }

        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        userMapper.insert(user);

        return convertToDTO(user);
    }

    public UserDTO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToDTO(user);
    }

    public UserDTO updateProfile(Long userId, UserDTO userDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }
        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }
        userMapper.updateById(user);
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
