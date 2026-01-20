package com.example.UberComp.dto.ride;

import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.Ride;
import com.example.UberComp.model.Route;
import com.example.UberComp.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@NoArgsConstructor
public class GetRideDetailsDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Route route;
    private Set<CreatedUserDTO> passengers;
    private Double price;
    private RideStatus status;
    private Boolean canceled;

    public GetRideDetailsDTO(Ride ride){
        id = ride.getId();
        startTime = ride.getStart();
        endTime = ride.getEstimatedTimeArrival();
        route = ride.getRoute();
        passengers = new HashSet<>();
        for(User user : ride.getPassengers()){
            passengers.add(new CreatedUserDTO(user));
        }
        price = ride.getPrice();
        status = ride.getStatus();
        if (ride.getCancellationReason()!=null)
            canceled = true;
        else
            canceled = false;
    }
}
