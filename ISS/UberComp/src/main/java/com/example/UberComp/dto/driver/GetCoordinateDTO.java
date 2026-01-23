package com.example.UberComp.dto.driver;

import com.example.UberComp.model.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCoordinateDTO {
    private Long id;
    private Double lat;
    private Double lon;
    private String address;
    private LocalDateTime cachedAt;

    public GetCoordinateDTO(Coordinate coordinate) {
        this.id = coordinate.getId();
        this.lat = coordinate.getLat();
        this.lon = coordinate.getLon();
        this.address = coordinate.getAddress();
        this.cachedAt = coordinate.getCachedAt();
    }
}