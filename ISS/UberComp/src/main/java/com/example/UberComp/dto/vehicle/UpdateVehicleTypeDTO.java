package com.example.UberComp.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateVehicleTypeDTO {
    public Long id;
    public String name;
    public Double price;
}
