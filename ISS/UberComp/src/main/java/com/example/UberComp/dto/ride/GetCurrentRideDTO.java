package com.example.UberComp.dto.ride;

import com.example.UberComp.model.Ride;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GetCurrentRideDTO {
    private Long id;
    private String name;
    private String lastname;
    private String startLocation;
    private String email;
    private String endLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public GetCurrentRideDTO(GetRideDTO ride, String name, String lastname, String email){
        this.id = ride.getId();
        this.startLocation = ride.getStartLocation();
        this.endLocation = ride.getEndLocation();
        this.startTime = ride.getStartTime();
        this.endTime = ride.getEndTime();
        this.name = name;
        this.lastname = lastname;
        this.email = email;
    }
}
