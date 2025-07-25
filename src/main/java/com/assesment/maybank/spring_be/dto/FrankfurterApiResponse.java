package com.assesment.maybank.spring_be.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class FrankfurterApiResponse {

    @NotNull
    private Double amount;

    @NotBlank
    private String base;

    @NotNull
    private LocalDate date;

    @NotNull
    @Size(min = 1, message = "Rates map must contain at least one entry")
    private Map<@NotBlank String, @DecimalMin("0.0") Double> rates;

}
