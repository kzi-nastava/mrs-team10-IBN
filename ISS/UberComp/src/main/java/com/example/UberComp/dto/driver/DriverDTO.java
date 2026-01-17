package com.example.UberComp.dto.driver;

import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.model.Driver;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DriverDTO {
    private AccountDTO accountDTO;
    private CreatedUserDTO createUserDTO;
    private VehicleDTO vehicleDTO;
    private Integer uptime;

    public DriverDTO(Driver driver) {
        this.accountDTO = new AccountDTO(driver.getAccount());
        this.createUserDTO = new CreatedUserDTO(
                driver.getId(),
                driver.getName(),
                driver.getLastName(),
                driver.getHomeAddress(),
                driver.getPhone(),
                driver.getImage()
        );
        this.vehicleDTO = new VehicleDTO(driver.getVehicle());
        this.uptime = driver.getUptime();
    }
}