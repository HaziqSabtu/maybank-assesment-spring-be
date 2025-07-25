package com.assesment.maybank.spring_be.service;

import com.assesment.maybank.spring_be.dto.WeatherDto;

public interface WeatherService {

    WeatherDto getWeatherByLatitudeLongitude(double latitude, double longitude);

}