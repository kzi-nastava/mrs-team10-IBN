package com.example.UberComp.dto.driver;

import com.example.UberComp.model.Vehicle;
import com.example.UberComp.model.VehicleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDTO {
    @NotNull(message = "Vehicle type is required")
    @Valid
    private VehicleTypeDTO vehicleTypeDTO;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "License plate is required")
    private String plate;

    @NotNull(message = "Seat number is required")
    @Min(value = 1, message = "Seat number must be at least 1")
    @Max(value = 9, message = "Seat number must be at most 9")
    private Integer seatNumber;

    @NotNull(message = "Baby seat field is required")
    private Boolean babySeat;

    @NotNull(message = "Pet friendly field is required")
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