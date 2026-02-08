package com.example.ubercorp.dto;

public class RideOrderResponseDTO {
    private Long rideId;
    private Double price;
    private String status;

    private String driverName;
    private String driverPhone;

    private String vehicleModel;
    private String vehiclePlate;

    private VehicleLocationDTO vehicleLocation;

    private Long estimatedPickupMinutes;
    private String estimatedPickupTime;

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public VehicleLocationDTO getVehicleLocation() {
        return vehicleLocation;
    }

    public void setVehicleLocation(VehicleLocationDTO vehicleLocation) {
        this.vehicleLocation = vehicleLocation;
    }

    public Long getEstimatedPickupMinutes() {
        return estimatedPickupMinutes;
    }

    public void setEstimatedPickupMinutes(Long estimatedPickupMinutes) {
        this.estimatedPickupMinutes = estimatedPickupMinutes;
    }

    public String getEstimatedPickupTime() {
        return estimatedPickupTime;
    }

    public void setEstimatedPickupTime(String estimatedPickupTime) {
        this.estimatedPickupTime = estimatedPickupTime;
    }
}
