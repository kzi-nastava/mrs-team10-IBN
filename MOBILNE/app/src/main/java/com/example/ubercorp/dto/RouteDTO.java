package com.example.ubercorp.dto;

import java.util.List;

public class RouteDTO {
    private Long id;
    List<CoordinateDTO> stations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CoordinateDTO> getStations() {
        return stations;
    }

    public void setStations(List<CoordinateDTO> stations) {
        this.stations = stations;
    }
}
