package com.example.ubercorp.dto;

import java.util.List;

public class StationsDTO {
    private Long id;

    public Long getId() {
        return id;
    }

    public List<CoordinateDTO> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<CoordinateDTO> coordinates) {
        this.coordinates = coordinates;
    }

    List<CoordinateDTO> coordinates;
}
