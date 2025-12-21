package com.example.UberComp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Coordinate {
    private Long id;
    private Double lat;
    private Double lon;
    private String address;
}
