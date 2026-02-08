package com.example.UberComp.dto.driver;

import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.model.Driver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DriverDTO {
    private AccountDTO accountDTO;
    private CreatedUserDTO createUserDTO;
    private VehicleDTO vehicleDTO;
    private Integer uptime;
    private boolean blocked;
    private String reason;
}