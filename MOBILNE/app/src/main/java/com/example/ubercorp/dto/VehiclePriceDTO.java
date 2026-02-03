package com.example.ubercorp.dto;

public class VehiclePriceDTO {
    private Long id;
    private String name;
    private double price;

    public VehiclePriceDTO(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getVehicleType() {
        return name;
    }

    public void setVehicleType(String vehicleType) {
        this.name = vehicleType;
    }
}
