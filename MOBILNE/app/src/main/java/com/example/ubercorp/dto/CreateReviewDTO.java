package com.example.ubercorp.dto;

public class CreateReviewDTO {
    public Long rideId;
    public int vehicleRating;
    public int driverRating;
    public String comment;

    public CreateReviewDTO(Long rideId, int vehicleRating, int driverRating, String comment) {
        this.rideId = rideId;
        this.vehicleRating = vehicleRating;
        this.driverRating = driverRating;
        this.comment = comment;
    }
}
