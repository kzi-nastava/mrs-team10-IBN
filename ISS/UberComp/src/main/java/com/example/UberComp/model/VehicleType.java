package com.example.UberComp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class VehicleType {
    private Long id;
    private String name;
    private Double price;
}
