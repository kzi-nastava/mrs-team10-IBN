package com.example.UberComp.dto.driver;

import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DriverDTO {
    private AccountDTO accountDTO;
    private CreatedUserDTO createUserDTO;
    private VehicleDTO vehicleDTO;
    private Integer uptime;
}
