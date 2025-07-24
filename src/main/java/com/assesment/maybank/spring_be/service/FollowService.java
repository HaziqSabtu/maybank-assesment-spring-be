package com.assesment.maybank.spring_be.service;

import java.util.List;
import java.util.UUID;

import com.assesment.maybank.spring_be.dto.FollowerDto;

public interface FollowService {
    void follow(UUID followerId, UUID followeeId);

    void unfollow(UUID followerId, UUID followeeId);

    List<FollowerDto> getFollowers(UUID followeeId);

    List<FollowerDto> getFollowees(UUID followerId);
}
