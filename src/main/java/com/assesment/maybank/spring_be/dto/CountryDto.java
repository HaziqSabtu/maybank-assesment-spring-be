package com.assesment.maybank.spring_be.dto;

import java.util.List;

public record CountryDto(
        String commonName,
        String officialName,
        List<String> capital,
        String cca2,
        Double latitude,
        Double longitude) {
}
