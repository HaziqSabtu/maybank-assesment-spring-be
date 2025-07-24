package com.assesment.maybank.spring_be.controller;

import com.assesment.maybank.spring_be.dto.FollowerDto;
import com.assesment.maybank.spring_be.dto.UserDto;
import com.assesment.maybank.spring_be.service.FollowService;
import com.assesment.maybank.spring_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<List<FollowerDto>> getFollowers(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/{userId}/followees")
    public ResponseEntity<List<FollowerDto>> getFollowees(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(followService.getFollowees(userId));
    }
}
