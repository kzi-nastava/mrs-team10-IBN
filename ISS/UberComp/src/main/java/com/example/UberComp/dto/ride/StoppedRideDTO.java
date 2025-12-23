package com.example.UberComp.dto.ride;

import com.example.UberComp.model.Coordinate;
import com.example.UberComp.model.Route;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StoppedRideDTO {
    private Long id;
    private Route route;
    private LocalDateTime finishTime;
    private Double price;
}
