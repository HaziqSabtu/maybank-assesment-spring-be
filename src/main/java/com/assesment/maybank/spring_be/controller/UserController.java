package com.assesment.maybank.spring_be.controller;

import com.assesment.maybank.spring_be.dto.FollowRequest;
import com.assesment.maybank.spring_be.dto.FollowStatusDto;
import com.assesment.maybank.spring_be.dto.FollowerDto;
import com.assesment.maybank.spring_be.dto.UserCreateRequest;
import com.assesment.maybank.spring_be.dto.UserDto;
import com.assesment.maybank.spring_be.service.FollowService;
import com.assesment.maybank.spring_be.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

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

    @GetMapping("/{userId}/followees/{followeeId}")
    public ResponseEntity<FollowStatusDto> getFollowStatus(@PathVariable("userId") UUID userId,
            @PathVariable("followeeId") UUID followeeId) {
        return ResponseEntity.ok(followService.getFollowStatus(userId, followeeId));
    }

    @PostMapping("/follow")
    public ResponseEntity<FollowStatusDto> follow(@RequestBody @Valid FollowRequest data) {

        // TODO: Do authentication
        UUID followerId = UUID.fromString("6285A781-2AA0-46FF-914A-18703F52DA17");
        UUID followeeId = data.getUserId();

        return ResponseEntity.ok(followService.follow(followerId, followeeId));
    }

    @DeleteMapping("/follow")
    public ResponseEntity<FollowStatusDto> unfollow(@RequestBody @Valid FollowRequest data) {

        // TODO: Do authentication
        UUID followerId = UUID.fromString("6285A781-2AA0-46FF-914A-18703F52DA17");
        UUID followeeId = data.getUserId();

        return ResponseEntity.ok(followService.unfollow(followerId, followeeId));
    }

}
