package com.example.ubercorp.dto;

public class CancelRideDTO {
    private Long id;
    private String cancellationReason;
    private boolean cancelledByDriver;

    public CancelRideDTO(Long id, String cancellationReason, boolean cancelledByDriver) {
        this.id = id;
        this.cancellationReason = cancellationReason;
        this.cancelledByDriver = cancelledByDriver;
    }
}
