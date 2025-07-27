package com.assesment.maybank.spring_be.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.assesment.maybank.spring_be.dto.PlaceCreateRequest;
import com.assesment.maybank.spring_be.dto.PlaceDeleteRequest;
import com.assesment.maybank.spring_be.dto.PlaceDto;

public interface PlaceService {
    PlaceDto createPlace(PlaceCreateRequest request, UUID userId);

    void deletePlace(PlaceDeleteRequest request, UUID userId);

    Page<PlaceDto> getPlaces(UUID userId, Pageable pageable);
}
