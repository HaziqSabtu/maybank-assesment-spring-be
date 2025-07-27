package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.PlaceCreateRequest;
import com.assesment.maybank.spring_be.dto.PlaceDeleteRequest;
import com.assesment.maybank.spring_be.dto.PlaceDto;
import com.assesment.maybank.spring_be.entity.Place;
import com.assesment.maybank.spring_be.entity.PlaceId;
import com.assesment.maybank.spring_be.exception.PlaceNotFoundException;
import com.assesment.maybank.spring_be.repository.PlaceRepository;
import com.assesment.maybank.spring_be.util.PageableUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceImplTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceServiceImpl placeService;

    private UUID userId;
    private String placeId;
    private PlaceCreateRequest createRequest;
    private PlaceDeleteRequest deleteRequest;
    private Place mockPlace;
    private PlaceId mockPlaceId;
    private Pageable pageable;
    private OffsetDateTime createdAt;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        placeId = "place123";
        mockPlaceId = new PlaceId(userId, placeId);
        createdAt = OffsetDateTime.now();

        createRequest = new PlaceCreateRequest();
        createRequest.setId(placeId);
        createRequest.setName("Test Place");
        createRequest.setAddress("123 Test Street");
        createRequest.setCategory("Restaurant");

        deleteRequest = new PlaceDeleteRequest();
        deleteRequest.setId(placeId);

        mockPlace = Place.builder()
                .id(mockPlaceId)
                .name("Test Place")
                .address("123 Test Street")
                .category("Restaurant")
                .createdAt(createdAt)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createPlace_ShouldCreateNewPlace_WhenPlaceDoesNotExist() {

        when(placeRepository.findById(mockPlaceId)).thenReturn(Optional.empty());
        when(placeRepository.save(any(Place.class))).thenReturn(mockPlace);

        PlaceDto result = placeService.createPlace(createRequest, userId);

        assertNotNull(result);
        assertEquals(placeId, result.id());
        assertEquals("Test Place", result.name());
        assertEquals("123 Test Street", result.address());
        assertEquals("Restaurant", result.category());
        assertEquals(createdAt, result.createdAt());

        verify(placeRepository).findById(mockPlaceId);
        verify(placeRepository).save(argThat(place -> place.getId().equals(mockPlaceId) &&
                place.getName().equals("Test Place") &&
                place.getAddress().equals("123 Test Street") &&
                place.getCategory().equals("Restaurant")));
    }

    @Test
    void createPlace_ShouldUpdateExistingPlace_WhenPlaceExists() {

        Place existingPlace = Place.builder()
                .id(mockPlaceId)
                .name("Old Name")
                .address("Old Address")
                .category("Old Category")
                .createdAt(createdAt.minusHours(1))
                .build();

        when(placeRepository.findById(mockPlaceId)).thenReturn(Optional.of(existingPlace));
        when(placeRepository.save(any(Place.class))).thenReturn(mockPlace);

        PlaceDto result = placeService.createPlace(createRequest, userId);

        assertNotNull(result);
        assertEquals(placeId, result.id());
        assertEquals("Test Place", result.name());
        assertEquals("123 Test Street", result.address());
        assertEquals("Restaurant", result.category());

        verify(placeRepository).findById(mockPlaceId);
        verify(placeRepository).save(argThat(place -> place.getId().equals(mockPlaceId) &&
                place.getName().equals("Test Place") &&
                place.getAddress().equals("123 Test Street") &&
                place.getCategory().equals("Restaurant")));
    }

    @Test
    void createPlace_ShouldHandleDifferentPlaceIds() {

        String differentPlaceId = "place456";
        PlaceId differentPlaceIdObj = new PlaceId(userId, differentPlaceId);

        createRequest.setId(differentPlaceId);
        createRequest.setName("Different Place");

        Place differentPlace = Place.builder()
                .id(differentPlaceIdObj)
                .name("Different Place")
                .address("123 Test Street")
                .category("Restaurant")
                .createdAt(createdAt)
                .build();

        when(placeRepository.findById(differentPlaceIdObj)).thenReturn(Optional.empty());
        when(placeRepository.save(any(Place.class))).thenReturn(differentPlace);

        PlaceDto result = placeService.createPlace(createRequest, userId);

        assertNotNull(result);
        assertEquals(differentPlaceId, result.id());
        assertEquals("Different Place", result.name());

        verify(placeRepository).findById(differentPlaceIdObj);
    }

    @Test
    void deletePlace_ShouldDeletePlace_WhenPlaceExists() {

        when(placeRepository.findById(mockPlaceId)).thenReturn(Optional.of(mockPlace));
        doNothing().when(placeRepository).delete(mockPlace);

        placeService.deletePlace(deleteRequest, userId);

        verify(placeRepository).findById(mockPlaceId);
        verify(placeRepository).delete(mockPlace);
    }

    @Test
    void deletePlace_ShouldThrowPlaceNotFoundException_WhenPlaceDoesNotExist() {

        when(placeRepository.findById(mockPlaceId)).thenReturn(Optional.empty());

        PlaceNotFoundException exception = assertThrows(PlaceNotFoundException.class,
                () -> placeService.deletePlace(deleteRequest, userId));

        assertEquals("Place not found", exception.getMessage());
        verify(placeRepository).findById(mockPlaceId);
        verify(placeRepository, never()).delete(any());
    }

    @Test
    void deletePlace_ShouldHandleDifferentPlaceIds() {

        String differentPlaceId = "place789";
        PlaceId differentPlaceIdObj = new PlaceId(userId, differentPlaceId);
        PlaceDeleteRequest differentDeleteRequest = new PlaceDeleteRequest();
        differentDeleteRequest.setId(differentPlaceId);

        Place differentPlace = Place.builder()
                .id(differentPlaceIdObj)
                .name("Different Place")
                .build();

        when(placeRepository.findById(differentPlaceIdObj)).thenReturn(Optional.of(differentPlace));
        doNothing().when(placeRepository).delete(differentPlace);

        placeService.deletePlace(differentDeleteRequest, userId);

        verify(placeRepository).findById(differentPlaceIdObj);
        verify(placeRepository).delete(differentPlace);
    }

    @Test
    void getPlaces_ShouldReturnPageOfPlaceDto_WhenPlacesExist() {

        Place place1 = Place.builder()
                .id(new PlaceId(userId, "place1"))
                .name("Place 1")
                .address("Address 1")
                .category("Category 1")
                .createdAt(createdAt)
                .build();

        Place place2 = Place.builder()
                .id(new PlaceId(userId, "place2"))
                .name("Place 2")
                .address("Address 2")
                .category("Category 2")
                .createdAt(createdAt.plusHours(1))
                .build();

        List<Place> places = Arrays.asList(place1, place2);
        Page<Place> mockPage = new PageImpl<>(places, pageable, 2);
        Pageable safeSortPageable = PageRequest.of(0, 10);

        try (MockedStatic<PageableUtils> pageableUtilsMock = mockStatic(PageableUtils.class)) {
            pageableUtilsMock.when(() -> PageableUtils.enforceStableSort(pageable, "createdAt", "id.placeId"))
                    .thenReturn(safeSortPageable);

            when(placeRepository.findByIdUserId(safeSortPageable, userId)).thenReturn(mockPage);

            Page<PlaceDto> result = placeService.getPlaces(userId, pageable);

            assertNotNull(result);
            assertEquals(2, result.getContent().size());

            PlaceDto dto1 = result.getContent().get(0);
            assertEquals("place1", dto1.id());
            assertEquals("Place 1", dto1.name());
            assertEquals("Address 1", dto1.address());
            assertEquals("Category 1", dto1.category());

            PlaceDto dto2 = result.getContent().get(1);
            assertEquals("place2", dto2.id());
            assertEquals("Place 2", dto2.name());

            verify(placeRepository).findByIdUserId(safeSortPageable, userId);
            pageableUtilsMock.verify(() -> PageableUtils.enforceStableSort(pageable, "createdAt", "id.placeId"));
        }
    }

    @Test
    void getPlaces_ShouldReturnEmptyPage_WhenNoPlacesExist() {

        Page<Place> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);
        Pageable safeSortPageable = PageRequest.of(0, 10);

        try (MockedStatic<PageableUtils> pageableUtilsMock = mockStatic(PageableUtils.class)) {
            pageableUtilsMock.when(() -> PageableUtils.enforceStableSort(pageable, "createdAt", "id.placeId"))
                    .thenReturn(safeSortPageable);

            when(placeRepository.findByIdUserId(safeSortPageable, userId)).thenReturn(emptyPage);

            Page<PlaceDto> result = placeService.getPlaces(userId, pageable);

            assertNotNull(result);
            assertEquals(0, result.getContent().size());
            assertEquals(0, result.getTotalElements());

            verify(placeRepository).findByIdUserId(safeSortPageable, userId);
        }
    }

    @Test
    void getPlace_ShouldReturnPlaceDto_WhenPlaceExists() {

        when(placeRepository.findById(mockPlaceId)).thenReturn(Optional.of(mockPlace));

        PlaceDto result = placeService.getPlace(placeId, userId);

        assertNotNull(result);
        assertEquals(placeId, result.id());
        assertEquals("Test Place", result.name());
        assertEquals("123 Test Street", result.address());
        assertEquals("Restaurant", result.category());
        assertEquals(createdAt, result.createdAt());

        verify(placeRepository).findById(mockPlaceId);
    }

    @Test
    void getPlace_ShouldThrowPlaceNotFoundException_WhenPlaceDoesNotExist() {

        when(placeRepository.findById(mockPlaceId)).thenReturn(Optional.empty());

        PlaceNotFoundException exception = assertThrows(PlaceNotFoundException.class,
                () -> placeService.getPlace(placeId, userId));

        assertEquals("Place not found", exception.getMessage());
        verify(placeRepository).findById(mockPlaceId);
    }

    @Test
    void getPlace_ShouldHandleDifferentUserIds() {

        UUID differentUserId = UUID.randomUUID();
        PlaceId differentPlaceId = new PlaceId(differentUserId, placeId);

        Place differentUserPlace = Place.builder()
                .id(differentPlaceId)
                .name("Different User Place")
                .address("Different Address")
                .category("Different Category")
                .createdAt(createdAt)
                .build();

        when(placeRepository.findById(differentPlaceId)).thenReturn(Optional.of(differentUserPlace));

        PlaceDto result = placeService.getPlace(placeId, differentUserId);

        assertNotNull(result);
        assertEquals(placeId, result.id());
        assertEquals("Different User Place", result.name());

        verify(placeRepository).findById(differentPlaceId);
    }

    @Test
    void createPlace_ShouldUpdateCreatedAtTime_WhenUpdatingExistingPlace() {

        OffsetDateTime oldCreatedAt = createdAt.minusHours(2);
        Place existingPlace = Place.builder()
                .id(mockPlaceId)
                .name("Old Name")
                .address("Old Address")
                .category("Old Category")
                .createdAt(oldCreatedAt)
                .build();

        when(placeRepository.findById(mockPlaceId)).thenReturn(Optional.of(existingPlace));
        when(placeRepository.save(any(Place.class))).thenAnswer(invocation -> {
            Place savedPlace = invocation.getArgument(0);
            // Verify that createdAt was updated to current time (approximately)
            assertTrue(savedPlace.getCreatedAt().isAfter(oldCreatedAt));
            return savedPlace;
        });

        placeService.createPlace(createRequest, userId);

        verify(placeRepository).save(argThat(place -> place.getCreatedAt().isAfter(oldCreatedAt)));
    }

    @Test
    void toPlaceDto_ShouldMapAllFieldsCorrectly() {

        when(placeRepository.findById(mockPlaceId)).thenReturn(Optional.of(mockPlace));

        PlaceDto result = placeService.getPlace(placeId, userId);

        assertEquals(mockPlace.getId().getPlaceId(), result.id());
        assertEquals(mockPlace.getName(), result.name());
        assertEquals(mockPlace.getAddress(), result.address());
        assertEquals(mockPlace.getCategory(), result.category());
        assertEquals(mockPlace.getCreatedAt(), result.createdAt());
    }
}