package com.example.UberComp.dto.driver;

import com.example.UberComp.dto.vehicle.VehicleLocationDTO;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableDriverDTO {
    private DriverDTO driver;
    private Long estimatedPickupMinutes;
    private VehicleLocationDTO vehicleLocation;
}