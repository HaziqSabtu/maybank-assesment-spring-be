package com.assesment.maybank.spring_be.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.assesment.maybank.spring_be.dto.LoginDto;
import com.assesment.maybank.spring_be.dto.LoginRequest;
import com.assesment.maybank.spring_be.dto.RegisterRequest;
import com.assesment.maybank.spring_be.entity.User;
import com.assesment.maybank.spring_be.exception.LoginFailedException;
import com.assesment.maybank.spring_be.exception.UserNotFoundException;
import com.assesment.maybank.spring_be.service.AuthService;
import com.assesment.maybank.spring_be.service.UserService;
import com.assesment.maybank.spring_be.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    @Override
    public LoginDto login(LoginRequest request) {

        User user;
        try {
            user = userService.getUserEntityByUsername(request.getUsername());
        } catch (UserNotFoundException e) {
            throw new LoginFailedException();
        }

        UUID userId = user.getId();
        String username = user.getUsername();
        String password = user.getPassword();

        if (!password.equals(request.getPassword())) {
            throw new LoginFailedException();
        }

        String token = jwtUtil.generateToken(userId);

        return new LoginDto(userId.toString(), username, token);

    }

    @Override
    public void register(RegisterRequest request) {
        userService.createUser(request);
        return;
    }

}
