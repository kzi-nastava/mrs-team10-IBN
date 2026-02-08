package com.example.UberComp.dto.ride;

import com.example.UberComp.model.Ride;
import com.example.UberComp.model.Route;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FinishedRideDTO {
    private Long id;
    private Route route;
    private LocalDateTime finishTime;
    private Double price;

    public FinishedRideDTO(Ride ride){
        this.id = ride.getId();
        this.route = ride.getRoute();
        this.finishTime = ride.getFinish();
        this.price = ride.getPrice();
    }
}
