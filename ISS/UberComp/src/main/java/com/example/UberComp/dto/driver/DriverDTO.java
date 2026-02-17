package com.example.UberComp.dto.driver;

import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.model.Driver;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DriverDTO {
    @NotNull(message = "Account data is required")
    @Valid
    private AccountDTO accountDTO;

    @NotNull(message = "User data is required")
    @Valid
    private CreatedUserDTO createUserDTO;

    @NotNull(message = "Vehicle data is required")
    @Valid
    private VehicleDTO vehicleDTO;
    private Integer uptime;
    private boolean blocked;
    private String reason;

    public DriverDTO(Driver driver) {
        this.uptime = driver.getUptime();
        this.reason = driver.getAccount().getBlockingReason();
        this.blocked = driver.getAccount().getAccountStatus().equals(AccountStatus.BLOCKED);
        this.vehicleDTO = new VehicleDTO(driver.getVehicle());
        this.createUserDTO = new CreatedUserDTO(driver);
        this.accountDTO = new AccountDTO(driver.getAccount());
    }
}