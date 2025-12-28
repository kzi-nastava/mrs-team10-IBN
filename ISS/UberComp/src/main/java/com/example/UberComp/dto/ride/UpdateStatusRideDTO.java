package com.example.UberComp.dto.ride;

import com.example.UberComp.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateStatusRideDTO {
    private RideStatus rideStatus;
}
