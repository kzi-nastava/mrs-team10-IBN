package com.example.UberComp.dto.vehicle;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleLocationDTO {
    private Double latitude;
    private Double longitude;
    private String address;
}