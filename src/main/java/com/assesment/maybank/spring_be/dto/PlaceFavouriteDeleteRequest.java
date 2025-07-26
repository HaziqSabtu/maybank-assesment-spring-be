package com.assesment.maybank.spring_be.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlaceFavouriteDeleteRequest {

    @NotBlank(message = "id is required")
    @Size(min = 1, message = "id must be at least 1 character")
    private String id;
}
