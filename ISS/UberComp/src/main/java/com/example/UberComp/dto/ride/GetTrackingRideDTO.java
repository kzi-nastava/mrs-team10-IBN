package com.example.UberComp.dto.ride;

import com.example.UberComp.model.Route;

import java.time.LocalDateTime;

public class GetTrackingRideDTO {
    private Route stations;
    private LocalDateTime estimatedTimeArrival;
}
