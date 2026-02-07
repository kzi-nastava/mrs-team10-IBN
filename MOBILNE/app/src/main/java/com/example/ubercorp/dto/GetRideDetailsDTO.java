package com.example.ubercorp.dto;

import com.example.ubercorp.activities.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.Set;

public class GetRideDetailsDTO {
    private Long id;
    private String startTime;
    private String endTime;
    private RouteDTO route;
    private Set<CreatedUserDTO> passengers;
    private Double price;
    private RideStatus status;
    private Boolean canceled;
    private Boolean panic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public RouteDTO getRoute() {
        return route;
    }

    public void setRoute(RouteDTO route) {
        this.route = route;
    }

    public Set<CreatedUserDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(Set<CreatedUserDTO> passengers) {
        this.passengers = passengers;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public Boolean getPanic() {
        return panic;
    }

    public void setPanic(Boolean panic) {
        this.panic = panic;
    }

    public boolean isCancelled(){
        return this.getStatus() == RideStatus.CancelledByDriver || this.getStatus() == RideStatus.CancelledByPassenger;
    }
}
