package com.assesment.maybank.spring_be.service;

import com.assesment.maybank.spring_be.dto.RegisterRequest;
import com.assesment.maybank.spring_be.dto.UserDto;
import com.assesment.maybank.spring_be.dto.UserSummaryDto;
import java.util.UUID;

import org.springframework.lang.Nullable;

public interface UserService {

    UserDto createUser(RegisterRequest request);

    UserDto getUserById(UUID userId);

    UserDto getUserByUsername(String username);

    @Nullable
    UserSummaryDto getUserSummary(UUID userId);
}