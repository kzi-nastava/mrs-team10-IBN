package com.example.UberComp.dto.ride;

import com.example.UberComp.dto.driver.CoordinateDTO;
import com.example.UberComp.dto.driver.GetCoordinateDTO;
import com.example.UberComp.model.Coordinate;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRideDTO {
    private GetCoordinateDTO startAddress;
    private GetCoordinateDTO destinationAddress;
    private Double distance;
    private List<GetCoordinateDTO> stops;
    private List<String> passengerEmails;
    private String vehicleType;
    private Boolean babySeat;
    private Boolean petFriendly;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduled;
    private Double price;
    private int estimatedDuration;
}
