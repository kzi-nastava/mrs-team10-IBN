package com.example.UberComp.dto.ride;

import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.model.Ride;
import com.example.UberComp.model.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetRideDTO {
    private Long Id;
    private String startLocation;
    private String endLocation;
    private Double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Set<CreatedUserDTO> passengers;

    public GetRideDTO(Ride ride){
        Id = ride.getId();
        startLocation = ride.getRoute().getStations().get(0).getAddress();
        int length = ride.getRoute().getStations().size();
        endLocation = ride.getRoute().getStations().get(length - 1).getAddress();
        price = ride.getPrice();
        startTime = ride.getStart();
        endTime = ride.getEstimatedTimeArrival();
        passengers = new HashSet<>();
        for (User passenger : ride.getPassengers()){
            passengers.add(new CreatedUserDTO(passenger));
        }
    }
}
