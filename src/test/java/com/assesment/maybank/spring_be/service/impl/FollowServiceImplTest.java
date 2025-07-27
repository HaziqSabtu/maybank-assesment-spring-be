package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.FollowStatusDto;
import com.assesment.maybank.spring_be.dto.FollowerDto;
import com.assesment.maybank.spring_be.entity.Follow;
import com.assesment.maybank.spring_be.entity.FollowId;
import com.assesment.maybank.spring_be.entity.User;
import com.assesment.maybank.spring_be.exception.UserNotFoundException;
import com.assesment.maybank.spring_be.projection.FollowerProjection;
import com.assesment.maybank.spring_be.repository.FollowRepository;
import com.assesment.maybank.spring_be.repository.UserRepository;
import com.assesment.maybank.spring_be.util.PageableUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceImplTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowServiceImpl followService;

    private UUID followerId;
    private UUID followeeId;
    private User followerUser;
    private User followeeUser;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        followerId = UUID.randomUUID();
        followeeId = UUID.randomUUID();

        followerUser = new User();
        followerUser.setId(followerId);
        followerUser.setUsername("follower");

        followeeUser = new User();
        followeeUser.setId(followeeId);
        followeeUser.setUsername("followee");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void follow_ShouldReturnFollowStatusDto_WhenUsersExistAndNotAlreadyFollowing() {

        when(userRepository.findById(followerId)).thenReturn(Optional.of(followerUser));
        when(userRepository.findById(followeeId)).thenReturn(Optional.of(followeeUser));
        when(followRepository.existsByFollowerAndFollowee(followerUser, followeeUser)).thenReturn(false);
        when(followRepository.save(any(Follow.class))).thenReturn(null);

        FollowStatusDto result = followService.follow(followerId, followeeId);

        assertNotNull(result);
        assertEquals(followerId, result.followerId());
        assertEquals(followeeId, result.followeeId());
        assertTrue(result.following());

        verify(userRepository).findById(followerId);
        verify(userRepository).findById(followeeId);
        verify(followRepository).existsByFollowerAndFollowee(followerUser, followeeUser);
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    void follow_ShouldReturnFollowStatusDto_WhenAlreadyFollowing() {

        when(userRepository.findById(followerId)).thenReturn(Optional.of(followerUser));
        when(userRepository.findById(followeeId)).thenReturn(Optional.of(followeeUser));
        when(followRepository.existsByFollowerAndFollowee(followerUser, followeeUser)).thenReturn(true);

        FollowStatusDto result = followService.follow(followerId, followeeId);

        assertNotNull(result);
        assertEquals(followerId, result.followerId());
        assertEquals(followeeId, result.followeeId());
        assertTrue(result.following());

        verify(userRepository).findById(followerId);
        verify(userRepository).findById(followeeId);
        verify(followRepository).existsByFollowerAndFollowee(followerUser, followeeUser);
        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    void follow_ShouldThrowIllegalArgumentException_WhenUserTriesToFollowThemself() {

        UUID sameUserId = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> followService.follow(sameUserId, sameUserId));

        assertEquals("Users cannot follow themselves.", exception.getMessage());
        verify(userRepository, never()).findById(any());
        verify(followRepository, never()).existsByFollowerAndFollowee(any(), any());
    }

    @Test
    void follow_ShouldThrowUserNotFoundException_WhenFollowerNotFound() {

        when(userRepository.findById(followerId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> followService.follow(followerId, followeeId));

        assertEquals("Follower not found with id: " + followerId, exception.getMessage());
        verify(userRepository).findById(followerId);
        verify(userRepository, never()).findById(followeeId);
    }

    @Test
    void follow_ShouldThrowUserNotFoundException_WhenFolloweeNotFound() {

        when(userRepository.findById(followerId)).thenReturn(Optional.of(followerUser));
        when(userRepository.findById(followeeId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> followService.follow(followerId, followeeId));

        assertEquals("Followee not found with id: " + followeeId, exception.getMessage());
        verify(userRepository).findById(followerId);
        verify(userRepository).findById(followeeId);
    }

    @Test
    void unfollow_ShouldReturnFollowStatusDto_WhenUsersExistAndCurrentlyFollowing() {

        when(userRepository.findById(followerId)).thenReturn(Optional.of(followerUser));
        when(userRepository.findById(followeeId)).thenReturn(Optional.of(followeeUser));
        when(followRepository.existsByFollowerAndFollowee(followerUser, followeeUser)).thenReturn(true);
        doNothing().when(followRepository).deleteByFollowerAndFollowee(followerUser, followeeUser);

        FollowStatusDto result = followService.unfollow(followerId, followeeId);

        assertNotNull(result);
        assertEquals(followerId, result.followerId());
        assertEquals(followeeId, result.followeeId());
        assertFalse(result.following());

        verify(userRepository).findById(followerId);
        verify(userRepository).findById(followeeId);
        verify(followRepository).existsByFollowerAndFollowee(followerUser, followeeUser);
        verify(followRepository).deleteByFollowerAndFollowee(followerUser, followeeUser);
    }

    @Test
    void unfollow_ShouldReturnFollowStatusDto_WhenNotCurrentlyFollowing() {

        when(userRepository.findById(followerId)).thenReturn(Optional.of(followerUser));
        when(userRepository.findById(followeeId)).thenReturn(Optional.of(followeeUser));
        when(followRepository.existsByFollowerAndFollowee(followerUser, followeeUser)).thenReturn(false);

        FollowStatusDto result = followService.unfollow(followerId, followeeId);

        assertNotNull(result);
        assertEquals(followerId, result.followerId());
        assertEquals(followeeId, result.followeeId());
        assertFalse(result.following());

        verify(userRepository).findById(followerId);
        verify(userRepository).findById(followeeId);
        verify(followRepository).existsByFollowerAndFollowee(followerUser, followeeUser);
        verify(followRepository, never()).deleteByFollowerAndFollowee(any(), any());
    }

    @Test
    void unfollow_ShouldThrowIllegalArgumentException_WhenUserTriesToUnfollowThemself() {

        UUID sameUserId = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> followService.unfollow(sameUserId, sameUserId));

        assertEquals("Users cannot follow themselves.", exception.getMessage());
    }

    @Test
    void getFollowStatus_ShouldReturnTrue_WhenUserfollowing() {

        when(userRepository.findById(followerId)).thenReturn(Optional.of(followerUser));
        when(userRepository.findById(followeeId)).thenReturn(Optional.of(followeeUser));
        when(followRepository.existsByFollowerAndFollowee(followerUser, followeeUser)).thenReturn(true);

        FollowStatusDto result = followService.getFollowStatus(followerId, followeeId);

        assertNotNull(result);
        assertEquals(followerId, result.followerId());
        assertEquals(followeeId, result.followeeId());
        assertTrue(result.following());

        verify(followRepository).existsByFollowerAndFollowee(followerUser, followeeUser);
    }

    @Test
    void getFollowStatus_ShouldReturnFalse_WhenUserIsNotFollowing() {

        when(userRepository.findById(followerId)).thenReturn(Optional.of(followerUser));
        when(userRepository.findById(followeeId)).thenReturn(Optional.of(followeeUser));
        when(followRepository.existsByFollowerAndFollowee(followerUser, followeeUser)).thenReturn(false);

        FollowStatusDto result = followService.getFollowStatus(followerId, followeeId);

        assertNotNull(result);
        assertEquals(followerId, result.followerId());
        assertEquals(followeeId, result.followeeId());
        assertFalse(result.following());

        verify(followRepository).existsByFollowerAndFollowee(followerUser, followeeUser);
    }

    @Test
    void getFollowStatus_ShouldThrowIllegalArgumentException_WhenSameUser() {

        UUID sameUserId = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> followService.getFollowStatus(sameUserId, sameUserId));

        assertEquals("Users cannot follow themselves.", exception.getMessage());
    }

    @Test
    void getFollowers_ShouldReturnPageOfFollowerDto_WhenFolloweeExists() {

        FollowerProjection projection1 = mock(FollowerProjection.class);
        when(projection1.getId()).thenReturn(UUID.randomUUID());
        when(projection1.getUsername()).thenReturn("follower1");

        FollowerProjection projection2 = mock(FollowerProjection.class);
        when(projection2.getId()).thenReturn(UUID.randomUUID());
        when(projection2.getUsername()).thenReturn("follower2");

        List<FollowerProjection> projections = Arrays.asList(projection1, projection2);
        Page<FollowerProjection> mockPage = new PageImpl<>(projections, pageable, 2);

        Pageable safeSortPageable = PageRequest.of(0, 10);

        when(userRepository.findById(followeeId)).thenReturn(Optional.of(followeeUser));

        try (MockedStatic<PageableUtils> pageableUtilsMock = mockStatic(PageableUtils.class)) {
            pageableUtilsMock.when(() -> PageableUtils.enforceStableSort(pageable, "f.followedAt", "u.id"))
                    .thenReturn(safeSortPageable);

            when(followRepository.findFollowerProjectionsByFolloweeId(followeeId, safeSortPageable))
                    .thenReturn(mockPage);

            Page<FollowerDto> result = followService.getFollowers(followeeId, pageable);

            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals("follower1", result.getContent().get(0).username());
            assertEquals("follower2", result.getContent().get(1).username());

            verify(userRepository).findById(followeeId);
            verify(followRepository).findFollowerProjectionsByFolloweeId(followeeId, safeSortPageable);
            pageableUtilsMock.verify(() -> PageableUtils.enforceStableSort(pageable, "f.followedAt", "u.id"));
        }
    }

    @Test
    void getFollowers_ShouldThrowUserNotFoundException_WhenFolloweeNotFound() {

        when(userRepository.findById(followeeId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> followService.getFollowers(followeeId, pageable));

        verify(userRepository).findById(followeeId);
        verify(followRepository, never()).findFollowerProjectionsByFolloweeId(any(), any());
    }

    @Test
    void getFollowees_ShouldReturnPageOfFollowerDto_WhenFollowerExists() {

        FollowerProjection projection1 = mock(FollowerProjection.class);
        when(projection1.getId()).thenReturn(UUID.randomUUID());
        when(projection1.getUsername()).thenReturn("followee1");

        FollowerProjection projection2 = mock(FollowerProjection.class);
        when(projection2.getId()).thenReturn(UUID.randomUUID());
        when(projection2.getUsername()).thenReturn("followee2");

        List<FollowerProjection> projections = Arrays.asList(projection1, projection2);
        Page<FollowerProjection> mockPage = new PageImpl<>(projections, pageable, 2);

        Pageable safeSortPageable = PageRequest.of(0, 10);

        when(userRepository.findById(followerId)).thenReturn(Optional.of(followerUser));

        try (MockedStatic<PageableUtils> pageableUtilsMock = mockStatic(PageableUtils.class)) {
            pageableUtilsMock.when(() -> PageableUtils.enforceStableSort(pageable, "f.followedAt", "u.id"))
                    .thenReturn(safeSortPageable);

            when(followRepository.findFollowerProjectionsByFollowerId(followerId, safeSortPageable))
                    .thenReturn(mockPage);

            Page<FollowerDto> result = followService.getFollowees(followerId, pageable);

            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals("followee1", result.getContent().get(0).username());
            assertEquals("followee2", result.getContent().get(1).username());

            verify(userRepository).findById(followerId);
            verify(followRepository).findFollowerProjectionsByFollowerId(followerId, safeSortPageable);
            pageableUtilsMock.verify(() -> PageableUtils.enforceStableSort(pageable, "f.followedAt", "u.id"));
        }
    }

    @Test
    void getFollowees_ShouldThrowUserNotFoundException_WhenFollowerNotFound() {

        when(userRepository.findById(followerId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> followService.getFollowees(followerId, pageable));

        verify(userRepository).findById(followerId);
        verify(followRepository, never()).findFollowerProjectionsByFollowerId(any(), any());
    }

    @Test
    void follow_ShouldCreateCorrectFollowEntity() {

        when(userRepository.findById(followerId)).thenReturn(Optional.of(followerUser));
        when(userRepository.findById(followeeId)).thenReturn(Optional.of(followeeUser));
        when(followRepository.existsByFollowerAndFollowee(followerUser, followeeUser)).thenReturn(false);

        followService.follow(followerId, followeeId);

        verify(followRepository).save(argThat(follow -> {
            FollowId followId = follow.getId();
            return followId.getFollowerId().equals(followerId) &&
                    followId.getFolloweeId().equals(followeeId) &&
                    follow.getFollower().equals(followerUser) &&
                    follow.getFollowee().equals(followeeUser);
        }));
    }
}