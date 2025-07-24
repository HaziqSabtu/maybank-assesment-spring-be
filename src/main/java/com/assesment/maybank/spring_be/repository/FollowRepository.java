package com.assesment.maybank.spring_be.repository;

import com.assesment.maybank.spring_be.entity.Follow;
import com.assesment.maybank.spring_be.entity.FollowId;
import com.assesment.maybank.spring_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    boolean existsByFollowerAndFollowee(User follower, User followee);

    void deleteByFollowerAndFollowee(User follower, User followee);

    List<Follow> findByFollower(User follower);

    List<Follow> findByFollowee(User followee);
}
