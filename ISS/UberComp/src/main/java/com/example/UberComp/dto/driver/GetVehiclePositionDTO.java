package com.example.UberComp.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetVehiclePositionDTO {
    private Long id;
    private Double lat;
    private Double lon;
    private boolean isBusy;
}
