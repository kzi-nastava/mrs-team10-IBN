package com.example.UberComp.dto.ride;

import lombok.Data;

@Data
public class CancelRideDTO {
    private Long id;
    private String cancellationReason;
    private boolean cancelledByDriver;
}
