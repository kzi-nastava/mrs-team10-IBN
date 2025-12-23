package com.example.UberComp.dto.driver;

import com.example.UberComp.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdatedStatusDriverDTO {
    private Long id;
    private DriverStatus status;
}
