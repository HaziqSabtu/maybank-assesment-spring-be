package com.assesment.maybank.spring_be.repository;

import com.assesment.maybank.spring_be.entity.Follow;
import com.assesment.maybank.spring_be.entity.FollowId;
import com.assesment.maybank.spring_be.entity.User;
import com.assesment.maybank.spring_be.projection.FollowerProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    boolean existsByFollowerAndFollowee(User follower, User followee);

    void deleteByFollowerAndFollowee(User follower, User followee);

    @Query("""
                SELECT new com.assesment.maybank.spring_be.projection.FollowerProjection(u.id, u.username)
                FROM Follow f
                JOIN User u ON u.id = f.id.followerId
                WHERE f.id.followeeId = :followeeId
            """)
    Page<FollowerProjection> findFollowerProjectionsByFolloweeId(@Param("followeeId") UUID followeeId,
            Pageable pageable);

    @Query("""
                SELECT new com.assesment.maybank.spring_be.projection.FollowerProjection(u.id, u.username)
                FROM Follow f
                JOIN User u ON u.id = f.id.followeeId
                WHERE f.id.followerId = :followerId
            """)
    Page<FollowerProjection> findFollowerProjectionsByFollowerId(@Param("followerId") UUID followerId,
            Pageable pageable);

    int countFollowersByFolloweeId(UUID followeeId);

    int countFolloweesByFollowerId(UUID followerId);

}
