package com.example.UberComp.dto.driver;

import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateDriverDTO {
    private String password;
    private CreatedUserDTO createUserDTO;
    private VehicleDTO vehicleDTO;
}
