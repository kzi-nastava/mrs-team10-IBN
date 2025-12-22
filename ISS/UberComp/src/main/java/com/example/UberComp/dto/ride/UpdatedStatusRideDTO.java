package com.example.UberComp.dto.ride;

import com.example.UberComp.enums.RideStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UpdatedStatusRideDTO {
    private Long id;
    private RideStatus rideStatus;
}
