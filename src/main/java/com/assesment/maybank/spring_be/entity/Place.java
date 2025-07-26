package com.assesment.maybank.spring_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

import org.springframework.lang.Nullable;

@Entity
@Table(name = "places", schema = "dbo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {

    @EmbeddedId
    private PlaceId id;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String name;

    @Column(columnDefinition = "VARCHAR(MAX)")
    @Nullable
    private String address;

    @Column(columnDefinition = "VARCHAR(MAX)")
    @Nullable
    private String category;

    @Column(name = "created_at", columnDefinition = "datetimeoffset")
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

}
