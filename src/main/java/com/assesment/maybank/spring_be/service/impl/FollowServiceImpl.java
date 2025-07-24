package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.FollowerDto;
import com.assesment.maybank.spring_be.entity.*;
import com.assesment.maybank.spring_be.projection.FollowerProjection;
import com.assesment.maybank.spring_be.repository.FollowRepository;
import com.assesment.maybank.spring_be.repository.UserRepository;
import com.assesment.maybank.spring_be.service.FollowService;
import com.assesment.maybank.spring_be.util.PageableUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    public void follow(UUID followerId, UUID followeeId) {
        if (followerId.equals(followeeId)) {
            throw new IllegalArgumentException("Users cannot follow themselves.");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new IllegalArgumentException("Followee not found"));

        if (followRepository.existsByFollowerAndFollowee(follower, followee))
            return;

        Follow follow = Follow.builder().id(new FollowId(followerId, followeeId)).follower(follower).followee(followee)
                .build();

        followRepository.save(follow);
    }

    @Override
    public void unfollow(UUID followerId, UUID followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new IllegalArgumentException("Followee not found"));

        followRepository.deleteByFollowerAndFollowee(follower, followee);
    }

    @Override
    public Page<FollowerDto> getFollowers(UUID followeeId, Pageable pageable) {
        userRepository.findById(followeeId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable safeSort = PageableUtils.enforceStableSort(pageable, "f.followedAt", "u.id");

        Page<FollowerProjection> follows = followRepository.findFollowerProjectionsByFolloweeId(followeeId, safeSort);

        return follows.map(follow -> new FollowerDto(follow.getId(), follow.getUsername()));
    }

    @Override
    public Page<FollowerDto> getFollowees(UUID followerId, Pageable pageable) {
        userRepository.findById(followerId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable safeSort = PageableUtils.enforceStableSort(pageable, "f.followedAt", "u.id");

        Page<FollowerProjection> follows = followRepository.findFollowerProjectionsByFollowerId(followerId, safeSort);

        return follows.map(follow -> new FollowerDto(follow.getId(), follow.getUsername()));
    }
}
