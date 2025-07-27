package com.assesment.maybank.spring_be.dto;

import java.time.OffsetDateTime;

import org.springframework.lang.Nullable;

public record PlaceDto(String id, String name, @Nullable String address, @Nullable String category,
        OffsetDateTime createdAt) {
}
