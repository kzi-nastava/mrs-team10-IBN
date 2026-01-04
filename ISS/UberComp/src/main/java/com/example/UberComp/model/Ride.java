package com.example.UberComp.model;

import com.example.UberComp.enums.RideStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Route route;

    @ManyToMany
    private Set<User> passengers;

    @ManyToOne
    private User driver;

    @Column(nullable = false)
    private Boolean babies;

    @Column(nullable = false)
    private Boolean pets;

    @Column(nullable = false)
    private Boolean panic;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private LocalDateTime estimatedTimeArrival;

    @Column
    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @Column
    private String cancellationReason;

}
