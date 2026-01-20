package com.example.UberComp.dto.driver;

import com.example.UberComp.model.Route;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetRouteDTO {
    private Long id;
    private List<GetCoordinateDTO> stations;

    public GetRouteDTO(Route route) {
        this.id = route.getId();
        this.stations = route.getStations().stream()
                .map(GetCoordinateDTO::new)
                .collect(Collectors.toList());
    }
}