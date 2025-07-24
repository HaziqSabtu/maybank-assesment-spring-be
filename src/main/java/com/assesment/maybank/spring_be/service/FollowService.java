package com.assesment.maybank.spring_be.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.assesment.maybank.spring_be.dto.FollowStatusDto;
import com.assesment.maybank.spring_be.dto.FollowerDto;

public interface FollowService {
    FollowStatusDto follow(UUID followerId, UUID followeeId);

    FollowStatusDto unfollow(UUID followerId, UUID followeeId);

    FollowStatusDto getFollowStatus(UUID followerId, UUID followeeId);

    Page<FollowerDto> getFollowers(UUID followeeId, Pageable pageable);

    Page<FollowerDto> getFollowees(UUID followerId, Pageable pageable);
}
