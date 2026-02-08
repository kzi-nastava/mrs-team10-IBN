package com.example.UberComp.dto.vehicle;

import com.example.UberComp.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetVehicleTypeDTO {
    public Long id;
    public String name;
    public Double price;

    public GetVehicleTypeDTO(VehicleType vehicleType){
        this.id = vehicleType.getId();
        this.name = vehicleType.getName();
        this.price = vehicleType.getPrice();
    }
}
