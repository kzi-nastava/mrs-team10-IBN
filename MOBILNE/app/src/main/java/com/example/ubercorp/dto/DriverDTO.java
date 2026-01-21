package com.example.ubercorp.dto;

public class DriverDTO {
    private AccountDTO accountDTO;
    private CreateUserDTO createUserDTO;
    private VehicleDTO vehicleDTO;
    private Integer uptime;

    public AccountDTO getAccount() {
        return accountDTO;
    }

    public void setAccount(AccountDTO account) {
        this.accountDTO = account;
    }

    public CreateUserDTO getCreateUser() {
        return createUserDTO;
    }

    public void setCreateUser(CreateUserDTO createUser) {
        this.createUserDTO = createUser;
    }

    public VehicleDTO getVehicleDTO() {
        return vehicleDTO;
    }

    public void setVehicleDTO(VehicleDTO vehicleDTO) {
        this.vehicleDTO = vehicleDTO;
    }

    public Integer getUptime() {
        return uptime;
    }

    public void setUptime(Integer uptime) {
        this.uptime = uptime;
    }
}