package com.example.UberComp.dto.ride;

import com.example.UberComp.model.Coordinate;
import com.example.UberComp.model.Route;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StopRideDTO {
    private int passed;
    private double lat;
    private double lon;
    private String address;
    private String finishTime;
}
