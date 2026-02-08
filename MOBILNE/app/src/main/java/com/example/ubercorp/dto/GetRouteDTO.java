package com.example.ubercorp.dto;

import java.util.List;

public class GetRouteDTO {
    private Long id;
    private List<GetCoordinateDTO> stations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<GetCoordinateDTO> getStations() {
        return stations;
    }

    public void setStations(List<GetCoordinateDTO> stations) {
        this.stations = stations;
    }
}
