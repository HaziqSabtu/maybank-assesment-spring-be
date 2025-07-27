package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.OpenMeteoApiResponse;
import com.assesment.maybank.spring_be.dto.WeatherDto;
import com.assesment.maybank.spring_be.dto.WeatherDto.WeatherMetric;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceIImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ValidationUtils validationUtils;

    @InjectMocks
    private WeatherServiceIImpl weatherService;

    private OpenMeteoApiResponse mockResponse;
    private OpenMeteoApiResponse.CurrentWeather currentWeather;
    private OpenMeteoApiResponse.CurrentWeatherUnits currentWeatherUnits;

    @BeforeEach
    void setUp() {
        currentWeather = new OpenMeteoApiResponse.CurrentWeather();
        currentWeather.setTemperature(25.5);
        currentWeather.setWindspeed(10.2);
        currentWeather.setWinddirection(180);

        currentWeatherUnits = new OpenMeteoApiResponse.CurrentWeatherUnits();
        currentWeatherUnits.setTemperature("°C");
        currentWeatherUnits.setWindspeed("km/h");
        currentWeatherUnits.setWinddirection("°");

        mockResponse = new OpenMeteoApiResponse();
        mockResponse.setCurrentWeather(currentWeather);
        mockResponse.setCurrentWeatherUnits(currentWeatherUnits);
    }

    @Test
    void getWeatherByLatitudeLongitude_ShouldReturnWeatherDto_WhenValidResponse() {

        double lat = 40.7128;
        double lon = -74.0060;
        String expectedUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true&timezone=auto",
                lat, lon);

        ResponseEntity<OpenMeteoApiResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, OpenMeteoApiResponse.class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockResponse);

        WeatherDto result = weatherService.getWeatherByLatitudeLongitude(lat, lon);

        assertNotNull(result);
        assertEquals(25.5, result.temperature().value());
        assertEquals("°C", result.temperature().unit());
        assertEquals(10.2, result.windspeed().value());
        assertEquals("km/h", result.windspeed().unit());
        assertEquals(180.0, result.winddirection().value());
        assertEquals("°", result.winddirection().unit());

        verify(restTemplate).getForEntity(expectedUrl, OpenMeteoApiResponse.class);
        verify(validationUtils).validate(mockResponse);
    }

    @Test
    void getWeatherByLatitudeLongitude_ShouldThrowRuntimeException_WhenResponseBodyIsNull() {

        double lat = 40.7128;
        double lon = -74.0060;
        String expectedUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true&timezone=auto",
                lat, lon);

        ResponseEntity<OpenMeteoApiResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, OpenMeteoApiResponse.class))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> weatherService.getWeatherByLatitudeLongitude(lat, lon));

        assertEquals("Invalid weather API response", exception.getMessage());
        verify(restTemplate).getForEntity(expectedUrl, OpenMeteoApiResponse.class);
        verify(validationUtils, never()).validate(any());
    }

    @Test
    void getWeatherByLatitudeLongitude_ShouldCallValidationUtils_WhenResponseIsValid() {

        double lat = 51.5074;
        double lon = -0.1278;
        String expectedUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true&timezone=auto",
                lat, lon);

        ResponseEntity<OpenMeteoApiResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, OpenMeteoApiResponse.class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockResponse);

        weatherService.getWeatherByLatitudeLongitude(lat, lon);

        verify(validationUtils).validate(mockResponse);
    }

    @Test
    void getWeatherByLatitudeLongitude_ShouldHandleNegativeCoordinates() {

        double lat = -33.8688;
        double lon = 151.2093;
        String expectedUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true&timezone=auto",
                lat, lon);

        ResponseEntity<OpenMeteoApiResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(expectedUrl, OpenMeteoApiResponse.class))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockResponse);

        WeatherDto result = weatherService.getWeatherByLatitudeLongitude(lat, lon);

        assertNotNull(result);
        verify(restTemplate).getForEntity(expectedUrl, OpenMeteoApiResponse.class);
    }

    @Test
    void getWeatherByLatitudeLongitude_ShouldCreateCorrectWeatherMetrics() {

        double lat = 35.6762;
        double lon = 139.6503;
        ResponseEntity<OpenMeteoApiResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(OpenMeteoApiResponse.class)))
                .thenReturn(responseEntity);
        doNothing().when(validationUtils).validate(mockResponse);

        WeatherDto result = weatherService.getWeatherByLatitudeLongitude(lat, lon);

        WeatherMetric temperatureMetric = result.temperature();
        WeatherMetric windspeedMetric = result.windspeed();
        WeatherMetric winddirectionMetric = result.winddirection();

        assertNotNull(temperatureMetric);
        assertNotNull(windspeedMetric);
        assertNotNull(winddirectionMetric);

        assertEquals(25.5, temperatureMetric.value());
        assertEquals("°C", temperatureMetric.unit());
        assertEquals(10.2, windspeedMetric.value());
        assertEquals("km/h", windspeedMetric.unit());
        assertEquals(180.0, winddirectionMetric.value());
        assertEquals("°", winddirectionMetric.unit());
    }
}