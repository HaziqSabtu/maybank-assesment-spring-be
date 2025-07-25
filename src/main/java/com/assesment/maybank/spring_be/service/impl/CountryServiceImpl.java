package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.CountryDto;
import com.assesment.maybank.spring_be.dto.RestCountriesApiResponse;
import com.assesment.maybank.spring_be.service.CountryService;
import com.assesment.maybank.spring_be.util.ValidationUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final RestTemplate restTemplate;
    private final ValidationUtils validationUtils;

    @Override
    @Cacheable(cacheNames = "country", key = "#p0")
    public CountryDto getCountryByCode(String countryCode) {
        String url = "https://restcountries.com/v3.1/alpha/" + countryCode;
        ResponseEntity<RestCountriesApiResponse[]> response = restTemplate.getForEntity(url,
                RestCountriesApiResponse[].class);

        RestCountriesApiResponse[] body = response.getBody();
        if (body == null || body.length == 0) {
            throw new RuntimeException("No data found for country: " + countryCode);
        }
        validationUtils.validate(body[0]);

        String commonName = body[0].getName().getCommon();
        String officialName = body[0].getName().getOfficial();
        String cca2 = body[0].getCca2();
        Double latitude = body[0].getLatlng().get(0);
        Double longitude = body[0].getLatlng().get(1);
        List<String> capital = body[0].getCapital();

        return new CountryDto(commonName, officialName, capital, cca2, latitude, longitude);
    }
}
