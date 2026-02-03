package com.example.ubercorp.dto;

public class GetReviewDTO {
    private Long id;
    private String username;
    private String comment;
    private Double driverRating;
    private Double vehicleRating;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Double driverRating) {
        this.driverRating = driverRating;
    }

    public Double getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(Double vehicleRating) {
        this.vehicleRating = vehicleRating;
    }
}
