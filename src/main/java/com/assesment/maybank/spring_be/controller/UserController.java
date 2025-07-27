package com.assesment.maybank.spring_be.controller;

import com.assesment.maybank.spring_be.dto.FollowRequest;
import com.assesment.maybank.spring_be.dto.FollowStatusDto;
import com.assesment.maybank.spring_be.dto.FollowerDto;
import com.assesment.maybank.spring_be.dto.UserDto;
import com.assesment.maybank.spring_be.dto.UserSummaryDto;
import com.assesment.maybank.spring_be.service.FollowService;
import com.assesment.maybank.spring_be.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    @Operation(summary = "Get user profile by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "UUID of the user") @PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(summary = "Get user summary (based on country)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Summary retrieved successfully")
    })
    @GetMapping("/{userId}/summary")
    public ResponseEntity<UserSummaryDto> getUserSummary(
            @Parameter(description = "UUID of the user") @PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(userService.getUserSummary(userId));
    }

    @Operation(summary = "Get list of followers for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Followers retrieved")
    })
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<FollowerDto>> getFollowers(
            @Parameter(description = "UUID of the user") @PathVariable("userId") UUID userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowers(userId, pageable));
    }

    @Operation(summary = "Get list of followees for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Followees retrieved")
    })
    @GetMapping("/{userId}/followees")
    public ResponseEntity<Page<FollowerDto>> getFollowees(
            @Parameter(description = "UUID of the user") @PathVariable("userId") UUID userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowees(userId, pageable));
    }

    @Operation(summary = "Check if a user follows another user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Follow status returned")
    })
    @GetMapping("/{userId}/followees/{followeeId}")
    public ResponseEntity<FollowStatusDto> getFollowStatus(
            @Parameter(description = "UUID of the follower") @PathVariable("userId") UUID userId,
            @Parameter(description = "UUID of the followee") @PathVariable("followeeId") UUID followeeId) {
        return ResponseEntity.ok(followService.getFollowStatus(userId, followeeId));
    }

    @Operation(summary = "Follow another user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Follow successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/follow")
    public ResponseEntity<FollowStatusDto> follow(
            @Parameter(description = "Follow request payload") @RequestBody @Valid FollowRequest data) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID followerId = (UUID) authentication.getPrincipal();
        UUID followeeId = data.getUserId();

        return ResponseEntity.ok(followService.follow(followerId, followeeId));
    }

    @Operation(summary = "Unfollow a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unfollow successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @DeleteMapping("/follow")
    public ResponseEntity<FollowStatusDto> unfollow(
            @Parameter(description = "Unfollow request payload") @RequestBody @Valid FollowRequest data) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID followerId = (UUID) authentication.getPrincipal();
        UUID followeeId = data.getUserId();

        return ResponseEntity.ok(followService.unfollow(followerId, followeeId));
    }
}
