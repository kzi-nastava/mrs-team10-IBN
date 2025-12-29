package com.example.UberComp.dto.ride;

import com.example.UberComp.model.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTrackingRideDTO {
    private Route stations;
    private LocalDateTime estimatedTimeArrival;
}
