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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"lat", "lon"}))
public class Coordinate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lon;

    private String address;

    public Coordinate() {

    }

    public Coordinate(Double lat, Double lon, String address) {
        this.id = null;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        if (!address.toLowerCase().contains("novi sad")) {
            this.address += ", Novi Sad, Serbia";
        }
    }

    public Coordinate(GetCoordinateDTO coord) {
        this.id = null;
        this.lat = coord.getLat();
        this.lon = coord.getLon();
        this.address = coord.getAddress();
        if (!coord.getAddress().toLowerCase().contains("novi sad")) {
            this.address += ", Novi Sad, Serbia";
        }
    }
}
