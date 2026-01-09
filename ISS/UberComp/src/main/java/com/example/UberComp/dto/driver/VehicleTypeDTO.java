package com.example.UberComp.dto.driver;

import com.example.UberComp.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTypeDTO {
    private Long id;
    private String name;
    private Double price;

    public VehicleTypeDTO(VehicleType vehicleType) {
        this.id = vehicleType.getId();
        this.name = vehicleType.getName();
        this.price = vehicleType.getPrice();
    }
}
