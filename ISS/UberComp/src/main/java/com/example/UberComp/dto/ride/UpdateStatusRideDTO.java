package com.example.UberComp.dto.ride;

import com.example.UberComp.enums.RideStatus;
import lombok.Data;

@Data
public class UpdateStatusRideDTO {
    private RideStatus rideStatus;
}
