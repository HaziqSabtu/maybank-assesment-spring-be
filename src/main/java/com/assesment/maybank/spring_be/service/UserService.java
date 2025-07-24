package com.assesment.maybank.spring_be.service;

import com.assesment.maybank.spring_be.dto.UserDto;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<UserDto> getUserById(UUID userId);
}