package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.FollowerDto;
import com.assesment.maybank.spring_be.entity.*;
import com.assesment.maybank.spring_be.repository.FollowRepository;
import com.assesment.maybank.spring_be.repository.UserRepository;
import com.assesment.maybank.spring_be.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<FollowerDto> getFollowers(UUID followeeId) {
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return followRepository.findByFollowee(followee).stream()
                .map(follow -> new FollowerDto(follow.getId().getFollowerId(),
                        userRepository.findById(follow.getId().getFollowerId()).get().getUsername()))
                .toList();
    }

    @Override
    public List<FollowerDto> getFollowees(UUID followerId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return followRepository.findByFollower(follower).stream()
                .map(follow -> new FollowerDto(follow.getId().getFolloweeId(),
                        userRepository.findById(follow.getId().getFolloweeId()).get().getUsername()))
                .toList();
    }
}
