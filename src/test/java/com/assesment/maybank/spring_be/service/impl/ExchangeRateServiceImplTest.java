package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.ExchangeRateDto;
import com.assesment.maybank.spring_be.dto.FrankfurterApiResponse;
import com.assesment.maybank.spring_be.util.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ValidationUtils validationUtils;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    private FrankfurterApiResponse mockResponse;
    private Map<String, Double> mockRates;

    @BeforeEach
    void setUp() {
        mockRates = new HashMap<>();
        mockRates.put("USD", 1.2345);

        mockResponse = new FrankfurterApiResponse();
        mockResponse.setRates(mockRates);
    }

    @Test
    void getExchangeRateToUsdByCountry_ShouldReturnExchangeRateDto_WhenValidResponse() {

        String baseCurrency = "EUR";
        String expectedUrl = "https://api.frankfurter.dev/v1/latest?base=EUR&symbols=USD";

        ResponseEntity<FrankfurterApiResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, FrankfurterApiResponse.class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockResponse);

        ExchangeRateDto result = exchangeRateService.getExchangeRateToUsdByCountry(baseCurrency);

        assertNotNull(result);
        assertEquals("EUR", result.baseCurrency());
        assertEquals("USD", result.targetCurrency());
        assertEquals(1.2345, result.rate());

        verify(restTemplate).getForEntity(expectedUrl, FrankfurterApiResponse.class);
        verify(validationUtils).validate(mockResponse);
    }

    @Test
    void getExchangeRateToUsdByCountry_ShouldThrowRuntimeException_WhenResponseBodyIsNull() {

        String baseCurrency = "GBP";
        String expectedUrl = "https://api.frankfurter.dev/v1/latest?base=GBP&symbols=USD";

        ResponseEntity<FrankfurterApiResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, FrankfurterApiResponse.class))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> exchangeRateService.getExchangeRateToUsdByCountry(baseCurrency));

        assertEquals("Invalid exchange rate API response", exception.getMessage());
        verify(restTemplate).getForEntity(expectedUrl, FrankfurterApiResponse.class);
        verify(validationUtils, never()).validate(any());
    }

    @Test
    void getExchangeRateToUsdByCountry_ShouldThrowRuntimeException_WhenTargetCurrencyRateNotAvailable() {

        String baseCurrency = "JPY";
        String expectedUrl = "https://api.frankfurter.dev/v1/latest?base=JPY&symbols=USD";

        Map<String, Double> emptyRates = new HashMap<>();
        FrankfurterApiResponse responseWithoutUSD = new FrankfurterApiResponse();
        responseWithoutUSD.setRates(emptyRates);

        ResponseEntity<FrankfurterApiResponse> responseEntity = new ResponseEntity<>(responseWithoutUSD, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, FrankfurterApiResponse.class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(responseWithoutUSD);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> exchangeRateService.getExchangeRateToUsdByCountry(baseCurrency));

        assertEquals("Target currency rate not available in response", exception.getMessage());
        verify(restTemplate).getForEntity(expectedUrl, FrankfurterApiResponse.class);
        verify(validationUtils).validate(responseWithoutUSD);
    }

    @Test
    void getExchangeRateToUsdByCountry_ShouldCallValidationUtils_WhenResponseIsValid() {

        String baseCurrency = "CAD";
        String expectedUrl = "https://api.frankfurter.dev/v1/latest?base=CAD&symbols=USD";

        ResponseEntity<FrankfurterApiResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, FrankfurterApiResponse.class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockResponse);

        exchangeRateService.getExchangeRateToUsdByCountry(baseCurrency);

        verify(validationUtils).validate(mockResponse);
    }

    @Test
    void getExchangeRateToUsdByCountry_ShouldHandleDifferentBaseCurrencies() {

        String baseCurrency = "AUD";
        String expectedUrl = "https://api.frankfurter.dev/v1/latest?base=AUD&symbols=USD";

        mockRates.put("USD", 0.7892);
        mockResponse.setRates(mockRates);

        ResponseEntity<FrankfurterApiResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, FrankfurterApiResponse.class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockResponse);

        ExchangeRateDto result = exchangeRateService.getExchangeRateToUsdByCountry(baseCurrency);

        assertNotNull(result);
        assertEquals("AUD", result.baseCurrency());
        assertEquals("USD", result.targetCurrency());
        assertEquals(0.7892, result.rate());

        verify(restTemplate).getForEntity(expectedUrl, FrankfurterApiResponse.class);
    }

    @Test
    void getExchangeRateToUsdByCountry_ShouldCreateCorrectUrl_ForDifferentCurrencies() {

        String baseCurrency = "CHF";
        String expectedUrl = "https://api.frankfurter.dev/v1/latest?base=CHF&symbols=USD";

        ResponseEntity<FrankfurterApiResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, FrankfurterApiResponse.class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockResponse);

        exchangeRateService.getExchangeRateToUsdByCountry(baseCurrency);

        verify(restTemplate).getForEntity(expectedUrl, FrankfurterApiResponse.class);
    }

    @Test
    void getExchangeRateToUsdByCountry_ShouldHandleZeroExchangeRate() {

        String baseCurrency = "EUR";
        String expectedUrl = "https://api.frankfurter.dev/v1/latest?base=EUR&symbols=USD";

        mockRates.put("USD", 0.0);
        mockResponse.setRates(mockRates);

        ResponseEntity<FrankfurterApiResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, FrankfurterApiResponse.class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockResponse);

        ExchangeRateDto result = exchangeRateService.getExchangeRateToUsdByCountry(baseCurrency);

        assertNotNull(result);
        assertEquals(0.0, result.rate());
    }

    @Test
    void getExchangeRateToUsdByCountry_ShouldHandleHighPrecisionRates() {

        String baseCurrency = "EUR";
        String expectedUrl = "https://api.frankfurter.dev/v1/latest?base=EUR&symbols=USD";

        mockRates.put("USD", 1.123456789);
        mockResponse.setRates(mockRates);

        ResponseEntity<FrankfurterApiResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, FrankfurterApiResponse.class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockResponse);

        ExchangeRateDto result = exchangeRateService.getExchangeRateToUsdByCountry(baseCurrency);

        assertNotNull(result);
        assertEquals(1.123456789, result.rate());
    }
}