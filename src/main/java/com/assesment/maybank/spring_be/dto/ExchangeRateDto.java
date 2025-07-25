package com.assesment.maybank.spring_be.dto;

public record ExchangeRateDto(
        String baseCurrency,
        String targetCurrency,
        double rate) {
}