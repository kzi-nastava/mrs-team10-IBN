package com.example.ubercorp.dto;

import java.time.LocalDateTime;

public class DriversRidesDTO {
    private Long id;
    private String name;
    private String lastname;
    private String startLocation;
    private String email;
    private String endLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public DriversRidesDTO(Long id, String name, String lastname, String startLocation, String email, String endLocation, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.startLocation = startLocation;
        this.email = email;
        this.endLocation = endLocation;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DriversRidesDTO(){}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
