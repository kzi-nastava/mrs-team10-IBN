package com.example.UberComp.dto.driver;

import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDriverDTO {
    @NotNull(message = "Account data is required")
    @Valid
    private AccountDTO accountDTO;

    @NotNull(message = "User data is required")
    @Valid
    private CreateUserDTO createUserDTO;

    @NotNull(message = "Vehicle data is required")
    @Valid
    private VehicleDTO vehicleDTO;
}
