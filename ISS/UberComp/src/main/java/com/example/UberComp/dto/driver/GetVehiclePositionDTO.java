package com.example.UberComp.dto.driver;

import com.example.UberComp.dto.vehicle.VehicleLocationDTO;
import com.example.UberComp.model.Ride;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GetVehiclePositionDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime estimatedTimeArrival;
    private RouteDTO route;
    @JsonProperty("isBusy")
    private boolean isBusy;
    private VehicleLocationDTO vehicleLocation;

    public GetVehiclePositionDTO(Ride ride, boolean isBusy){
        this.id = ride.getId();
        this.startTime = ride.getStart();
        this.estimatedTimeArrival = ride.getEstimatedTimeArrival();
        this.route = new RouteDTO(ride.getRoute());
        this.isBusy = isBusy;
        this.vehicleLocation = new VehicleLocationDTO(ride.getDriver().getVehicle().getLocation());
    }

    public GetVehiclePositionDTO() {

    }
}
