package com.assesment.maybank.spring_be.dto;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlaceCreateRequest {

    @NotBlank(message = "id is required")
    @Size(min = 1, message = "id must be at least 1 character")
    private String id;

    @NotBlank(message = "name is required")
    @Size(min = 1, message = "name must be at least 1 character")
    private String name;

    @Nullable
    private String category;

    @Nullable
    private String address;
}
