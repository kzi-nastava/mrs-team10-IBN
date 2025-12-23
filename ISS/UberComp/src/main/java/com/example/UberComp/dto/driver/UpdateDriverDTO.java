package com.example.UberComp.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateDriverDTO {
    private String name;
    private String lastName;
    private String homeAddress;
    private String phone;
    private String image;
    private VehicleDTO vehicleDTO;
}
