package com.assesment.maybank.spring_be.service;

import com.assesment.maybank.spring_be.dto.UserCreateRequest;
import com.assesment.maybank.spring_be.dto.UserDto;
import com.assesment.maybank.spring_be.dto.UserSummaryDto;

import java.util.UUID;

import org.springframework.lang.Nullable;

public interface UserService {

    UserDto createUser(UserCreateRequest request);

    UserDto getUserById(UUID userId);

    @Nullable
    UserSummaryDto getUserSummary(UUID userId);
}