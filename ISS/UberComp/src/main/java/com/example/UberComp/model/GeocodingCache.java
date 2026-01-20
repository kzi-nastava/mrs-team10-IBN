package com.example.UberComp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "geocoding_cache")
@Data
public class GeocodingCache {

    @Id
    @Column(length = 500)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "cached_at", nullable = false)
    private LocalDateTime cachedAt;
}