package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.CountryDto;
import com.assesment.maybank.spring_be.dto.RestCountriesApiResponse;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ValidationUtils validationUtils;

    @InjectMocks
    private CountryServiceImpl countryService;

    private RestCountriesApiResponse mockCountryResponse;
    private RestCountriesApiResponse.Name mockName;

    @BeforeEach
    void setUp() {
        mockName = new RestCountriesApiResponse.Name();
        mockName.setCommon("Malaysia");
        mockName.setOfficial("Malaysia");

        mockCountryResponse = new RestCountriesApiResponse();
        mockCountryResponse.setName(mockName);
        mockCountryResponse.setCca2("MY");
        mockCountryResponse.setLatlng(Arrays.asList(2.5, 112.5));
        mockCountryResponse.setCapital(Arrays.asList("Kuala Lumpur"));
    }

    @Test
    void getCountryByCode_ShouldReturnCountryDto_WhenValidResponse() {

        String countryCode = "MY";
        String expectedUrl = "https://restcountries.com/v3.1/alpha/MY";
        RestCountriesApiResponse[] responseArray = { mockCountryResponse };

        ResponseEntity<RestCountriesApiResponse[]> responseEntity = new ResponseEntity<>(responseArray,
                HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, RestCountriesApiResponse[].class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockCountryResponse);

        CountryDto result = countryService.getCountryByCode(countryCode);

        assertNotNull(result);
        assertEquals("Malaysia", result.commonName());
        assertEquals("Malaysia", result.officialName());
        assertEquals("MY", result.cca2());
        assertEquals(2.5, result.latitude());
        assertEquals(112.5, result.longitude());
        assertEquals(Arrays.asList("Kuala Lumpur"), result.capital());

        verify(restTemplate).getForEntity(expectedUrl, RestCountriesApiResponse[].class);
        verify(validationUtils).validate(mockCountryResponse);
    }

    @Test
    void getCountryByCode_ShouldThrowRuntimeException_WhenResponseBodyIsNull() {

        String countryCode = "US";
        String expectedUrl = "https://restcountries.com/v3.1/alpha/US";

        ResponseEntity<RestCountriesApiResponse[]> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, RestCountriesApiResponse[].class))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> countryService.getCountryByCode(countryCode));

        assertEquals("No data found for country: US", exception.getMessage());
        verify(restTemplate).getForEntity(expectedUrl, RestCountriesApiResponse[].class);
        verify(validationUtils, never()).validate(any());
    }

    @Test
    void getCountryByCode_ShouldThrowRuntimeException_WhenResponseArrayIsEmpty() {

        String countryCode = "XX";
        String expectedUrl = "https://restcountries.com/v3.1/alpha/XX";
        RestCountriesApiResponse[] emptyResponseArray = {};

        ResponseEntity<RestCountriesApiResponse[]> responseEntity = new ResponseEntity<>(emptyResponseArray,
                HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, RestCountriesApiResponse[].class))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> countryService.getCountryByCode(countryCode));

        assertEquals("No data found for country: XX", exception.getMessage());
        verify(restTemplate).getForEntity(expectedUrl, RestCountriesApiResponse[].class);
        verify(validationUtils, never()).validate(any());
    }

    @Test
    void getCountryByCode_ShouldCallValidationUtils_WhenResponseIsValid() {

        String countryCode = "SG";
        String expectedUrl = "https://restcountries.com/v3.1/alpha/SG";
        RestCountriesApiResponse[] responseArray = { mockCountryResponse };

        ResponseEntity<RestCountriesApiResponse[]> responseEntity = new ResponseEntity<>(responseArray,
                HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, RestCountriesApiResponse[].class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockCountryResponse);

        countryService.getCountryByCode(countryCode);

        verify(validationUtils).validate(mockCountryResponse);
    }

    @Test
    void getCountryByCode_ShouldHandleDifferentCountryCodes() {

        String countryCode = "GB";
        String expectedUrl = "https://restcountries.com/v3.1/alpha/GB";

        mockName.setCommon("United Kingdom");
        mockName.setOfficial("United Kingdom of Great Britain and Northern Ireland");
        mockCountryResponse.setName(mockName);
        mockCountryResponse.setCca2("GB");
        mockCountryResponse.setLatlng(Arrays.asList(54.0, -2.0));
        mockCountryResponse.setCapital(Arrays.asList("London"));

        RestCountriesApiResponse[] responseArray = { mockCountryResponse };

        ResponseEntity<RestCountriesApiResponse[]> responseEntity = new ResponseEntity<>(responseArray,
                HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, RestCountriesApiResponse[].class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockCountryResponse);

        CountryDto result = countryService.getCountryByCode(countryCode);

        assertNotNull(result);
        assertEquals("United Kingdom", result.commonName());
        assertEquals("United Kingdom of Great Britain and Northern Ireland", result.officialName());
        assertEquals("GB", result.cca2());
        assertEquals(54.0, result.latitude());
        assertEquals(-2.0, result.longitude());
        assertEquals(Arrays.asList("London"), result.capital());

        verify(restTemplate).getForEntity(expectedUrl, RestCountriesApiResponse[].class);
    }

    @Test
    void getCountryByCode_ShouldCreateCorrectUrl_ForDifferentCountryCodes() {

        String countryCode = "DE";
        String expectedUrl = "https://restcountries.com/v3.1/alpha/DE";
        RestCountriesApiResponse[] responseArray = { mockCountryResponse };

        ResponseEntity<RestCountriesApiResponse[]> responseEntity = new ResponseEntity<>(responseArray,
                HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, RestCountriesApiResponse[].class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockCountryResponse);

        countryService.getCountryByCode(countryCode);

        verify(restTemplate).getForEntity(expectedUrl, RestCountriesApiResponse[].class);
    }

    @Test
    void getCountryByCode_ShouldHandleMultipleCapitals() {

        String countryCode = "ZA";
        String expectedUrl = "https://restcountries.com/v3.1/alpha/ZA";

        mockName.setCommon("South Africa");
        mockName.setOfficial("Republic of South Africa");
        mockCountryResponse.setName(mockName);
        mockCountryResponse.setCca2("ZA");
        mockCountryResponse.setLatlng(Arrays.asList(-29.0, 24.0));
        mockCountryResponse.setCapital(Arrays.asList("Cape Town", "Pretoria", "Bloemfontein"));

        RestCountriesApiResponse[] responseArray = { mockCountryResponse };

        ResponseEntity<RestCountriesApiResponse[]> responseEntity = new ResponseEntity<>(responseArray,
                HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, RestCountriesApiResponse[].class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockCountryResponse);

        CountryDto result = countryService.getCountryByCode(countryCode);

        assertNotNull(result);
        assertEquals("South Africa", result.commonName());
        assertEquals("Republic of South Africa", result.officialName());
        assertEquals("ZA", result.cca2());
        assertEquals(-29.0, result.latitude());
        assertEquals(24.0, result.longitude());

        List<String> expectedCapitals = Arrays.asList("Cape Town", "Pretoria", "Bloemfontein");
        assertEquals(expectedCapitals, result.capital());
    }

    @Test
    void getCountryByCode_ShouldHandleNegativeCoordinates() {

        String countryCode = "AU";
        String expectedUrl = "https://restcountries.com/v3.1/alpha/AU";

        mockName.setCommon("Australia");
        mockName.setOfficial("Commonwealth of Australia");
        mockCountryResponse.setName(mockName);
        mockCountryResponse.setCca2("AU");
        mockCountryResponse.setLatlng(Arrays.asList(-27.0, 133.0));
        mockCountryResponse.setCapital(Arrays.asList("Canberra"));

        RestCountriesApiResponse[] responseArray = { mockCountryResponse };

        ResponseEntity<RestCountriesApiResponse[]> responseEntity = new ResponseEntity<>(responseArray,
                HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, RestCountriesApiResponse[].class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockCountryResponse);

        CountryDto result = countryService.getCountryByCode(countryCode);

        assertNotNull(result);
        assertEquals(-27.0, result.latitude());
        assertEquals(133.0, result.longitude());
    }

    @Test
    void getCountryByCode_ShouldExtractFirstElementFromArray_WhenMultipleCountriesReturned() {

        String countryCode = "FR";
        String expectedUrl = "https://restcountries.com/v3.1/alpha/FR";

        RestCountriesApiResponse secondCountry = new RestCountriesApiResponse();
        RestCountriesApiResponse.Name secondName = new RestCountriesApiResponse.Name();
        secondName.setCommon("Second Country");
        secondCountry.setName(secondName);

        RestCountriesApiResponse[] responseArray = { mockCountryResponse, secondCountry };

        ResponseEntity<RestCountriesApiResponse[]> responseEntity = new ResponseEntity<>(responseArray,
                HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, RestCountriesApiResponse[].class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockCountryResponse);

        CountryDto result = countryService.getCountryByCode(countryCode);

        assertNotNull(result);
        assertEquals("Malaysia", result.commonName()); // Should use first element
        verify(validationUtils).validate(mockCountryResponse); // Should validate first element only
        verify(validationUtils, never()).validate(secondCountry);
    }
}