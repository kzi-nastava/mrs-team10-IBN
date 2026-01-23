package com.example.UberComp.repository;

import com.example.UberComp.model.Coordinate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CoordinateRepository extends JpaRepository<Coordinate, Long> {
    Optional<Coordinate> findByAddressAndCachedAtAfter(String address, LocalDateTime after);
    Optional<Coordinate> findByLatAndLon(Double lat, Double lon);
}
