package com.example.UberComp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class VehicleChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Driver driver;

    @Column(columnDefinition = "TEXT")
    private String requestedVehicleChanges;

    @Column
    private String status;
}
