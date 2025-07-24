package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.UserCreateRequest;
import com.assesment.maybank.spring_be.dto.UserDto;
import com.assesment.maybank.spring_be.entity.User;
import com.assesment.maybank.spring_be.exception.UserNotFoundException;
import com.assesment.maybank.spring_be.repository.UserRepository;
import com.assesment.maybank.spring_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserCreateRequest request) {
        User user = User.builder().username(request.getUsername()).build();
        userRepository.save(user);

        int followerCount = 0;
        int followingCount = 0;
        return toDto(user, followerCount, followingCount);
    }

    @Override
    public UserDto getUserById(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        int followerCount = 0;
        int followingCount = 0;

        return toDto(user, followerCount, followingCount);
    }

    private UserDto toDto(User user, int followerCount, int followingCount) {
        return new UserDto(user.getId(), user.getUsername(), followerCount, followingCount);
    }

}
