package com.example.UberComp.dto.ride;

import com.example.UberComp.model.Ride;
import com.example.UberComp.model.Route;
import lombok.Data;

@Data
public class IncomingRideDTO {
    private Long id;
    private Route route;
    private String token;

    public IncomingRideDTO(Ride ride){
        this.id = ride.getId();
        this.route = ride.getRoute();
        this.token = ride.getTrackingToken();
    }
}
