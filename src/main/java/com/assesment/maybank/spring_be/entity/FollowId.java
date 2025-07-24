package com.assesment.maybank.spring_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FollowId implements Serializable {

    @Column(name = "follower_id", columnDefinition = "uniqueidentifier")
    private UUID followerId;

    @Column(name = "followee_id", columnDefinition = "uniqueidentifier")
    private UUID followeeId;
}
