package com.example.UberComp.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRideDTO {
    private String startAddress;
    private String destinationAddress;
    private List<String> stops;
    private List<String> passengerEmails;
    private String vehicleType;
    private Boolean babySeat;
    private Boolean petFriendly;
}
