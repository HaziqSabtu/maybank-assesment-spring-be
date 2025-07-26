package com.assesment.maybank.spring_be.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assesment.maybank.spring_be.dto.PlaceFavouriteCreateRequest;
import com.assesment.maybank.spring_be.dto.PlaceFavouriteDeleteRequest;
import com.assesment.maybank.spring_be.dto.PlaceFavouriteDto;
import com.assesment.maybank.spring_be.entity.Place;
import com.assesment.maybank.spring_be.entity.PlaceId;
import com.assesment.maybank.spring_be.exception.PlaceNotFoundException;
import com.assesment.maybank.spring_be.repository.PlaceRepository;
import com.assesment.maybank.spring_be.service.PlaceService;
import com.assesment.maybank.spring_be.util.PageableUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional
    public PlaceFavouriteDto createFavourite(PlaceFavouriteCreateRequest request, UUID userId) {
        PlaceId placeId = new PlaceId(userId, request.getId());
        Place place = placeRepository.findById(placeId)
                .orElse(null);

        if (place == null) {
            place = Place.builder()
                    .id(placeId)
                    .name(request.getName())
                    .address(request.getAddress())
                    .category(request.getCategory())
                    .build();
        } else {
            place.setName(request.getName());
            place.setAddress(request.getAddress());
            place.setCategory(request.getCategory());
        }

        place = placeRepository.save(place);

        return toPlaceFavouriteDto(place);
    }

    @Override
    public void deleteFavourite(PlaceFavouriteDeleteRequest request, UUID userId) {
        PlaceId placeId = new PlaceId(userId, request.getId());
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException("Place not found"));

        placeRepository.delete(place);
    }

    public Page<PlaceFavouriteDto> getFavourites(UUID userId, Pageable pageable) {

        Pageable safeSort = PageableUtils.enforceStableSort(pageable, "createdAt", "id.placeId");

        Page<Place> places = placeRepository.findByIdUserId(safeSort, userId);

        return places.map(this::toPlaceFavouriteDto);

    }

    private PlaceFavouriteDto toPlaceFavouriteDto(Place place) {

        return new PlaceFavouriteDto(place.getId().getPlaceId(), place.getName(), place.getAddress(),
                place.getCategory(), place.getCreatedAt());
    }

}
