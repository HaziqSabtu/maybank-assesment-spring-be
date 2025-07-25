package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.ExchangeRateDto;
import com.assesment.maybank.spring_be.dto.FrankfurterApiResponse;
import com.assesment.maybank.spring_be.service.ExchangeRateService;
import com.assesment.maybank.spring_be.util.ValidationUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final RestTemplate restTemplate;
    private final ValidationUtils validationUtils;

    @Cacheable(value = "exchangeRate", key = "#p0")
    public ExchangeRateDto getExchangeRateToUsdByCountry(String base) {
        String targetCurrency = "USD";
        String url = String.format("https://api.frankfurter.dev/v1/latest?base=%s&symbols=%s", base, targetCurrency);

        ResponseEntity<FrankfurterApiResponse> response = restTemplate.getForEntity(url, FrankfurterApiResponse.class);
        FrankfurterApiResponse body = response.getBody();

        if (body == null) {
            throw new RuntimeException("Invalid exchange rate API response");
        }

        validationUtils.validate(body);

        Map<String, Double> rates = body.getRates();
        Double rate = rates.get(targetCurrency);

        if (rate == null) {
            throw new RuntimeException("Target currency rate not available in response");
        }

        return new ExchangeRateDto(base, targetCurrency, rate);
    }
}
