package com.assesment.maybank.spring_be.service;

import com.assesment.maybank.spring_be.dto.RegisterRequest;
import com.assesment.maybank.spring_be.dto.UserDto;
import com.assesment.maybank.spring_be.dto.UserSummaryDto;
import com.assesment.maybank.spring_be.entity.User;

import java.util.UUID;

import org.springframework.lang.Nullable;

public interface UserService {

    UserDto createUser(RegisterRequest request);

    UserDto getUserById(UUID userId);

    User getUserEntityByUsername(String username);

    User getUserEntityById(UUID userId);

    @Nullable
    UserSummaryDto getUserSummary(UUID userId);
}