package com.example.UberComp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Vehicle {
    private Long id;
    private VehicleType vehicleType;
    private String model;
    private String plate;
    private Integer seatNumber;
    private Boolean babySeat;
    private Boolean petFriendly;
}
