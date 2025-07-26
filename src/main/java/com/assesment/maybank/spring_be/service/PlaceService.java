package com.assesment.maybank.spring_be.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.assesment.maybank.spring_be.dto.PlaceFavouriteCreateRequest;
import com.assesment.maybank.spring_be.dto.PlaceFavouriteDeleteRequest;
import com.assesment.maybank.spring_be.dto.PlaceFavouriteDto;

public interface PlaceService {
    PlaceFavouriteDto createFavourite(PlaceFavouriteCreateRequest request, UUID userId);

    void deleteFavourite(PlaceFavouriteDeleteRequest request, UUID userId);

    Page<PlaceFavouriteDto> getFavourites(UUID userId, Pageable pageable);
}
