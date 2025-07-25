package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.OpenMeteoApiResponse;
import com.assesment.maybank.spring_be.dto.WeatherDto;
import com.assesment.maybank.spring_be.dto.WeatherDto.WeatherMetric;
import com.assesment.maybank.spring_be.service.WeatherService;
import com.assesment.maybank.spring_be.util.ValidationUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WeatherServiceIImpl implements WeatherService {

    private final RestTemplate restTemplate;
    private final ValidationUtils validationUtils;

    @Cacheable(value = "weather", key = "T(java.util.Objects).hash(#lat, #lon)")
    public WeatherDto getWeatherByLatitudeLongitude(double lat, double lon) {
        String url = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true&timezone=auto",
                lat, lon);

        ResponseEntity<OpenMeteoApiResponse> response = restTemplate.getForEntity(url, OpenMeteoApiResponse.class);
        OpenMeteoApiResponse body = response.getBody();

        if (body == null) {
            throw new RuntimeException("Invalid weather API response");
        }

        validationUtils.validate(body);

        double temperature = body.getCurrentWeather().getTemperature();
        String temperatureUnit = body.getCurrentWeatherUnits().getTemperature();

        double windspeed = body.getCurrentWeather().getWindspeed();
        String windspeedUnit = body.getCurrentWeatherUnits().getWindspeed();

        double winddirection = body.getCurrentWeather().getWinddirection();
        String winddirectionUnit = body.getCurrentWeatherUnits().getWinddirection();

        WeatherMetric temperatureMetric = new WeatherMetric(temperature, temperatureUnit);
        WeatherMetric windspeedMetric = new WeatherMetric(windspeed, windspeedUnit);
        WeatherMetric winddirectionMetric = new WeatherMetric(winddirection, winddirectionUnit);

        return new WeatherDto(temperatureMetric, windspeedMetric, winddirectionMetric);

    }
}
