package com.example.UberComp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledRide extends Ride {

    @Column(nullable = false)
    private LocalDateTime scheduled;

    @Column
    private boolean reminder15Sent = false;

    @Column
    private boolean reminder10Sent = false;

    @Column
    private boolean reminder5Sent = false;

    @Column
    private LocalDateTime driverShouldLeaveAt;

    @Column
    private boolean driverNotified = false;
}

