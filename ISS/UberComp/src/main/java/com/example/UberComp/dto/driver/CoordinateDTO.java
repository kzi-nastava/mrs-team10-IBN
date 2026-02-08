package com.example.UberComp.dto.driver;

import com.example.UberComp.model.Coordinate;
import com.example.UberComp.model.Route;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordinateDTO {
    public Long id;
    public Double lon;
    public Double lat;

    public CoordinateDTO(Coordinate coordinate) {
        this.id = coordinate.getId();
        this.lat = coordinate.getLat();
        this.lon = coordinate.getLon();
    }
}
