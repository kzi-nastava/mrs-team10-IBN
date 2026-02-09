package com.example.ubercorp.dto;

public class CreateReportDTO {
    public Long rideId;
    public String content;

    public CreateReportDTO(Long rideId, String content) {
        this.rideId = rideId;
        this.content = content;
    }
}
