package com.example.UberComp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "driver_change_request")
public class DriverChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Driver driver;

    @Column(columnDefinition = "TEXT")
    private String requestedChanges;

    @Column
    private String status;

    @Column
    private LocalDateTime requestDate = LocalDateTime.now();
}

