package com.example.UberComp.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StartedRideDTO {
    private Long id;
    private LocalDateTime start;
}
