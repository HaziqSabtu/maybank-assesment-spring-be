package com.assesment.maybank.spring_be.controller;

import com.assesment.maybank.spring_be.dto.PlaceCreateRequest;
import com.assesment.maybank.spring_be.dto.PlaceDeleteRequest;
import com.assesment.maybank.spring_be.dto.PlaceDto;
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
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping
    public ResponseEntity<PlaceDto> createPlace(@RequestBody @Valid PlaceCreateRequest data) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(placeService.createPlace(data, userId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePlace(@RequestBody @Valid PlaceDeleteRequest data) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();

        placeService.deletePlace(data, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<Page<PlaceDto>> getPlaces(
            @PageableDefault(size = 6) Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(placeService.getPlaces(userId, pageable));
    }

}
