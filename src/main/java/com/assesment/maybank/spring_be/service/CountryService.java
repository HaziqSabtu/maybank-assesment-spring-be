package com.assesment.maybank.spring_be.service;

import com.assesment.maybank.spring_be.dto.CountryDto;

public interface CountryService {
    CountryDto getCountryByCode(String countryCode);
}
