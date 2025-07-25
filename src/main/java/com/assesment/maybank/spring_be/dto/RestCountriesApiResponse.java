package com.assesment.maybank.spring_be.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RestCountriesApiResponse {
    @NotNull
    @Valid
    private Name name;

    @NotBlank
    private String cca2;

    @Size(min = 2, max = 2, message = "latlng must contain exactly 2 values (latitude and longitude)")
    private List<@DecimalMin("-180.0") @DecimalMax("180.0") Double> latlng;

    @NotEmpty
    private List<String> capital;

    @Data
    public static class Name {

        @NotBlank
        private String common;

        @NotBlank
        private String official;
    }
}
