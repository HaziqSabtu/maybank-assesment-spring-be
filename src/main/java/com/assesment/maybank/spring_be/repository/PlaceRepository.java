package com.assesment.maybank.spring_be.repository;

import com.assesment.maybank.spring_be.entity.Place;
import com.assesment.maybank.spring_be.entity.PlaceId;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, PlaceId> {
    Page<Place> findByIdUserId(Pageable pageable, UUID userId);
}
