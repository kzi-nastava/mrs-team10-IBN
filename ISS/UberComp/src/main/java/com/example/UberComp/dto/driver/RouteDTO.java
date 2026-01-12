package com.example.UberComp.dto.driver;

import com.example.UberComp.model.Route;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteDTO {
    public Long id;
    List<CoordinateDTO> stations;

    public RouteDTO(Route route) {
        this.id = route.getId();
        this.stations = route.getStations()
                .stream()
                .map(CoordinateDTO::new)
                .toList();
    }
}
