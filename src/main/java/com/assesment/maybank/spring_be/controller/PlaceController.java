package com.assesment.maybank.spring_be.controller;

import com.assesment.maybank.spring_be.dto.PlaceCreateRequest;
import com.assesment.maybank.spring_be.dto.PlaceDeleteRequest;
import com.assesment.maybank.spring_be.dto.PlaceDto;
import com.assesment.maybank.spring_be.service.PlaceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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

        @Operation(summary = "Add a new place created by the authenticated user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Place successfully created"),
                        @ApiResponse(responseCode = "400", description = "Invalid input")
        })
        @PostMapping
        public ResponseEntity<PlaceDto> createPlace(
                        @Parameter(description = "Place creation payload") @RequestBody @Valid PlaceCreateRequest data) {

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UUID userId = (UUID) authentication.getPrincipal();

                return ResponseEntity.ok(placeService.createPlace(data, userId));
        }

        @Operation(summary = "Delete a place by ID (if owned by the user)")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Place successfully deleted"),
                        @ApiResponse(responseCode = "404", description = "Place not found")
        })
        @DeleteMapping
        public ResponseEntity<Void> deletePlace(
                        @Parameter(description = "Place deletion payload") @RequestBody @Valid PlaceDeleteRequest data) {

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UUID userId = (UUID) authentication.getPrincipal();

                placeService.deletePlace(data, userId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        @Operation(summary = "Get paginated list of saved places for authenticated user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "List of places retrieved")
        })
        @GetMapping
        public ResponseEntity<Page<PlaceDto>> getPlaces(
                        @Parameter(description = "Pagination settings") @PageableDefault(size = 10) Pageable pageable) {

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UUID userId = (UUID) authentication.getPrincipal();
                return ResponseEntity.ok(placeService.getPlaces(userId, pageable));
        }

        @Operation(summary = "Get details of a specific place by ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Place details retrieved"),
        })
        @GetMapping("/{placeId}")
        public ResponseEntity<PlaceDto> getPlace(
                        @Parameter(description = "Place ID (Google Place ID)") @PathVariable String placeId) {

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UUID userId = (UUID) authentication.getPrincipal();
                return ResponseEntity.ok(placeService.getPlace(placeId, userId));
        }
}
