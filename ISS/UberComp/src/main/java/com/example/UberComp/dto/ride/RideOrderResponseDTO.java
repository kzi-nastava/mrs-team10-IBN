package com.example.UberComp.dto.ride;

import com.example.UberComp.dto.vehicle.VehicleLocationDTO;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideOrderResponseDTO {
    private Long rideId;
    private Double price;
    private String status;

    private String driverName;
    private String driverPhone;

    private String vehicleModel;
    private String vehiclePlate;

    private VehicleLocationDTO vehicleLocation;

    private Long estimatedPickupMinutes;
    private String estimatedPickupTime;
}
