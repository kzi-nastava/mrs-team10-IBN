package com.example.UberComp.dto.driver;

import com.example.UberComp.dto.user.CreateUserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateDriverDTO {
    private String password;
    private CreateUserDTO createUserDTO;
    private VehicleDTO vehicleDTO;
}
