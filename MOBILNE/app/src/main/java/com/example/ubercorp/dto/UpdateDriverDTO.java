package com.example.ubercorp.dto;

public class UpdateDriverDTO {
    private CreateUserDTO createUserDTO;
    private VehicleDTO vehicleDTO;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UpdateDriverDTO(CreateUserDTO createUserDTO, VehicleDTO vehicleDTO, String password) {
        this.createUserDTO = createUserDTO;
        this.vehicleDTO = vehicleDTO;
        this.password = password;
    }

    public CreateUserDTO getCreateUserDTO() { return createUserDTO; }
    public VehicleDTO getVehicleDTO() { return vehicleDTO; }
}