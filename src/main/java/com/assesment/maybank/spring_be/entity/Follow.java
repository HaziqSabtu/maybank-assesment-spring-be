package com.assesment.maybank.spring_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "follows", schema = "dbo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {

    @EmbeddedId
    private FollowId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followeeId")
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    @Column(name = "followed_at", columnDefinition = "datetimeoffset")
    private OffsetDateTime followedAt;

    @PrePersist
    protected void onCreate() {
        this.followedAt = OffsetDateTime.now();
    }
}
