package com.example.UberComp.dto.ride;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class GetRideDTO {
    private Long Id;
    private String startLocation;
    private String endLocation;
    private Double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
