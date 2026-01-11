package com.example.UberComp.dto.driver;

import com.example.UberComp.model.Vehicle;
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

    public VehicleDTO(Vehicle vehicle) {
        this.vehicleTypeDTO = new VehicleTypeDTO(vehicle.getVehicleType());
        this.model = vehicle.getModel();
        this.plate = vehicle.getPlate();
        this.seatNumber = vehicle.getSeatNumber();
        this.babySeat = vehicle.getBabySeat();
        this.petFriendly = vehicle.getPetFriendly();
    }
}