package com.example.UberComp.repository;

import com.example.UberComp.model.GeocodingCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface GeocodingCacheRepository extends JpaRepository<GeocodingCache, String> {
    Optional<GeocodingCache> findByAddressAndCachedAtAfter(String address, LocalDateTime after);
}