package com.example.UberComp.model;

import com.example.UberComp.dto.vehicle.UpdateVehicleTypeDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Double price;

    public VehicleType(UpdateVehicleTypeDTO update){
        this.id = update.getId();
        this.price = update.getPrice();
        this.name = update.getName();
    }
}

