package com.example.ubercorp.dto;

public class CreateDriverDTO {
    private AccountDTO accountDTO;
    private CreateUserDTO createUserDTO;
    private VehicleDTO vehicleDTO;

    public CreateDriverDTO(AccountDTO accountDTO, CreateUserDTO createUserDTO, VehicleDTO vehicleDTO) {
        this.accountDTO = accountDTO;
        this.createUserDTO = createUserDTO;
        this.vehicleDTO = vehicleDTO;
    }

    public AccountDTO getAccountDTO() {
        return accountDTO;
    }

    public void setAccountDTO(AccountDTO accountDTO) {
        this.accountDTO = accountDTO;
    }

    public CreateUserDTO getCreateUserDTO() {
        return createUserDTO;
    }

    public void setCreateUserDTO(CreateUserDTO createUserDTO) {
        this.createUserDTO = createUserDTO;
    }

    public VehicleDTO getVehicleDTO() {
        return vehicleDTO;
    }

    public void setVehicleDTO(VehicleDTO vehicleDTO) {
        this.vehicleDTO = vehicleDTO;
    }
}
