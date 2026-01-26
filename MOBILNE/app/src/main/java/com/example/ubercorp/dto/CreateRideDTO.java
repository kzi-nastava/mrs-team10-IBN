package com.example.ubercorp.dto;

import java.util.List;

public class CreateRideDTO {
    private GetCoordinateDTO startAddress;
    private GetCoordinateDTO destinationAddress;
    private Double distance;
    private List<GetCoordinateDTO> stops;
    private List<String> passengerEmails;
    private String vehicleType;
    private Boolean babySeat;
    private Boolean petFriendly;
    private String scheduled;
    private Double price;
    private Integer estimatedDuration;

    public GetCoordinateDTO getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(GetCoordinateDTO startAddress) {
        this.startAddress = startAddress;
    }

    public GetCoordinateDTO getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(GetCoordinateDTO destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public List<GetCoordinateDTO> getStops() {
        return stops;
    }

    public void setStops(List<GetCoordinateDTO> stops) {
        this.stops = stops;
    }

    public List<String> getPassengerEmails() {
        return passengerEmails;
    }

    public void setPassengerEmails(List<String> passengerEmails) {
        this.passengerEmails = passengerEmails;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Boolean getBabySeat() {
        return babySeat;
    }

    public void setBabySeat(Boolean babySeat) {
        this.babySeat = babySeat;
    }

    public Boolean getPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(Boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }
}
