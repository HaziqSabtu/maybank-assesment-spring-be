package com.assesment.maybank.spring_be.dto;

import org.springframework.lang.Nullable;

public record UserSummaryDto(
        @Nullable CountryDto country,
        @Nullable WeatherDto weather,
        @Nullable ExchangeRateDto exchangeRate) {
}
