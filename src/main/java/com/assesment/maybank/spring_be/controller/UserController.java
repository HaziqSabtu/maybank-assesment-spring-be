package com.assesment.maybank.spring_be.controller;

import com.assesment.maybank.spring_be.dto.FollowerDto;
import com.assesment.maybank.spring_be.dto.UserDto;
import com.assesment.maybank.spring_be.service.FollowService;
import com.assesment.maybank.spring_be.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") UUID userId) {
        return userService.getUserById(userId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<FollowerDto>> getFollowers(@PathVariable("userId") UUID userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowers(userId, pageable));
    }

    @GetMapping("/{userId}/followees")
    public ResponseEntity<Page<FollowerDto>> getFollowees(@PathVariable("userId") UUID userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowees(userId, pageable));
    }
}
