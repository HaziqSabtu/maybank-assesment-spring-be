package com.assesment.maybank.spring_be.dto;

import java.util.UUID;

public record FollowStatusDto(UUID followerId, UUID followeeId, boolean following) {
}
