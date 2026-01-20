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

}

