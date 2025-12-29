package com.example.UberComp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Review {
    private Long id;
    private User user;
    private Ride ride;
    private Double driverRating;
    private Double vehicleRating;
    private String content;

}
