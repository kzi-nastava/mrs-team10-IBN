package com.example.UberComp.dto.driver;

import com.example.UberComp.model.VehicleType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDTO {
    private VehicleTypeDTO vehicleTypeDTO;
    private String model;
    private String plate;
    private Integer seatNumber;
    private Boolean babySeat;
    private Boolean petFriendly;
}