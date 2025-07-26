package com.assesment.maybank.spring_be.controller;

import com.assesment.maybank.spring_be.dto.PlaceFavouriteCreateRequest;
import com.assesment.maybank.spring_be.dto.PlaceFavouriteDeleteRequest;
import com.assesment.maybank.spring_be.dto.PlaceFavouriteDto;
import com.assesment.maybank.spring_be.service.PlaceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/favourites")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeFavouriteService;

    @PostMapping
    public ResponseEntity<PlaceFavouriteDto> createFavourite(@RequestBody @Valid PlaceFavouriteCreateRequest data) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(placeFavouriteService.createFavourite(data, userId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFavourite(@RequestBody @Valid PlaceFavouriteDeleteRequest data) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();

        placeFavouriteService.deleteFavourite(data, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<Page<PlaceFavouriteDto>> getFavourites(
            @PageableDefault(size = 6) Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(placeFavouriteService.getFavourites(userId, pageable));
    }

}
