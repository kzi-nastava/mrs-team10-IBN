package com.example.UberComp.dto.vehicle;

import com.example.UberComp.dto.driver.CoordinateDTO;
import com.example.UberComp.model.Coordinate;
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

    public VehicleLocationDTO(Coordinate coordinate){
        this.latitude = coordinate.getLat();
        this.longitude = coordinate.getLon();
        this.address = coordinate.getAddress();
    }
}