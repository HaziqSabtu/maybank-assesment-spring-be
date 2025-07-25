package com.assesment.maybank.spring_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OpenMeteoApiResponse {

    @JsonProperty("current_weather")
    @NotNull
    @Valid
    private CurrentWeather currentWeather;

    @JsonProperty("current_weather_units")
    @NotNull
    @Valid
    private CurrentWeatherUnits currentWeatherUnits;

    @Data
    public static class CurrentWeatherUnits {

        @NotBlank
        private String temperature;

        @NotBlank
        private String windspeed;

        @NotBlank
        private String winddirection;
    }

    @Data
    public static class CurrentWeather {

        @DecimalMin("-100.0")
        @DecimalMax("100.0")
        private double temperature;

        @DecimalMin("0.0")
        @DecimalMax("200.0")
        private double windspeed;

        @Min(0)
        @Max(360)
        private int winddirection;
    }
}
