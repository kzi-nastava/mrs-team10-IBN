package com.example.UberComp.dto.ride;

import com.example.UberComp.model.Coordinate;
import com.example.UberComp.model.Route;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StopRideDTO {
    private Long id;
    private Route passed;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
}
