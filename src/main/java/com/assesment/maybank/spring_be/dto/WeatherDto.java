package com.assesment.maybank.spring_be.dto;

public record WeatherDto(
        WeatherMetric temperature,
        WeatherMetric windspeed,
        WeatherMetric winddirection) {

    public record WeatherMetric(double value, String unit) {
    }
}
