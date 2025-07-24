package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.UserDto;
import com.assesment.maybank.spring_be.entity.User;
import com.assesment.maybank.spring_be.repository.UserRepository;
import com.assesment.maybank.spring_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<UserDto> getUserById(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        return Optional.ofNullable(user).map(this::toDto);
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername());
    }

}
