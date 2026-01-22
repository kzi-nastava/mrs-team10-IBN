package com.example.UberComp.model;

import com.example.UberComp.dto.driver.GetCoordinateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Coordinate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double lat;

    @Column
    private Double lon;

    @Column(unique = true)
    private String address;

    @Column(name = "cached_at", nullable = false)
    private LocalDateTime cachedAt;

    public Coordinate() {
        this.cachedAt = LocalDateTime.now();
    }

    public Coordinate(Double lat, Double lon) {
        this.id = null;
        this.lat = lat;
        this.lon = lon;
        this.cachedAt = LocalDateTime.now();
    }

    public Coordinate(GetCoordinateDTO coord) {
        this.id = coord.getId();
        this.cachedAt = coord.getCachedAt();
        this.lat = coord.getLat();
        this.lon = coord.getLon();
    }
}
