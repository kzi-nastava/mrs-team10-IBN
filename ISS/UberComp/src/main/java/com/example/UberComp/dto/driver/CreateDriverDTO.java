package com.example.UberComp.dto.driver;

import com.example.UberComp.dto.account.AccountDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateDriverDTO {
    private AccountDTO accountDTO;
    private String name;
    private String lastName;
    private String homeAddress;
    private String phone;
    private String image;
    private VehicleDTO vehicleDTO;
}
