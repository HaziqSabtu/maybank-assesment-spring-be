package com.assesment.maybank.spring_be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "dbo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(generator = "uuid4")
    @Column(nullable = false, columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(nullable = false, length = 255)
    private String username;

    @Column(columnDefinition = "datetimeoffset")
    private OffsetDateTime createdAt;

    @Column(columnDefinition = "datetimeoffset")
    private OffsetDateTime updatedAt;

    @Column(nullable = false, length = 2)
    private String countryCode;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
