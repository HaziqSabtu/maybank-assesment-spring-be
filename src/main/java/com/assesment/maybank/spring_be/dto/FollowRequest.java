package com.assesment.maybank.spring_be.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class FollowRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;
}
