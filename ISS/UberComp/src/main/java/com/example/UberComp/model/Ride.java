package com.example.UberComp.model;

import com.example.UberComp.enums.RideStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
public class Ride {
    private Long id;
    private Route route;
    private List<User> passengers;
    private User driver;
    private Boolean babies;
    private Boolean pets;
    private Double price;
    private LocalDateTime start;
    private LocalDateTime estimatedTimeArrival;
    private RideStatus status;
    private String cancellationReason;
}
