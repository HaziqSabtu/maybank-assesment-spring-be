package com.assesment.maybank.spring_be.service;

import com.assesment.maybank.spring_be.dto.ExchangeRateDto;

public interface ExchangeRateService {

    ExchangeRateDto getExchangeRateToUsdByCountry(String base);
}