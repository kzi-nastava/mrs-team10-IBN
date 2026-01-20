package com.example.ubercorp.dto;

public class UpdateDriverDTO {
    private CreateUserDTO user;
    private VehicleDTO vehicle;

    public UpdateDriverDTO(CreateUserDTO userDTO, VehicleDTO vehicleDTO) {
        this.user = userDTO;
        this.vehicle = vehicleDTO;
    }

    public CreateUserDTO getUserDTO() { return user; }
    public VehicleDTO getVehicleDTO() { return vehicle; }
}