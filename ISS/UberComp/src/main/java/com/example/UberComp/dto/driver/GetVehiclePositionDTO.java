package com.example.UberComp.dto.driver;

import com.example.UberComp.model.Ride;
import com.example.UberComp.model.Route;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GetVehiclePositionDTO {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime estimatedTimeArrival;
    private RouteDTO route;

    public GetVehiclePositionDTO(Ride ride){
        this.id = ride.getId();
        this.start = ride.getStart();
        this.estimatedTimeArrival = ride.getEstimatedTimeArrival();
        this.route = new RouteDTO(ride.getRoute());
    }
}
