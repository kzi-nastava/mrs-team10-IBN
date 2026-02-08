package com.example.ubercorp.dto;

public class DriverDTO {
    private AccountDTO accountDTO;
    private CreateUserDTO createUserDTO;
    private VehicleDTO vehicleDTO;
    private Integer uptime;
    private boolean blocked;
    private boolean reason;

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

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isReason() {
        return reason;
    }

    public void setReason(boolean reason) {
        this.reason = reason;
    }
}