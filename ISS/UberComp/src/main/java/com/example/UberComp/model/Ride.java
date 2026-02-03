package com.example.UberComp.model;

import com.example.UberComp.enums.RideStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Route route;

    @ManyToMany
    @JoinTable(
            name = "ride_passengers",
            joinColumns = @JoinColumn(name = "ride_id"),
            inverseJoinColumns = @JoinColumn(name = "passengers_id")
    )
    private Set<User> passengers = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "ride_guests",
            joinColumns = @JoinColumn(name = "ride_id")
    )
    @Column(name = "email")
    private Set<String> guestEmails = new HashSet<>();

    @ManyToOne
    private Driver driver;

    @Column(nullable = false)
    private Boolean babies;

    @Column(nullable = false)
    private Boolean pets;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private LocalDateTime estimatedTimeArrival;

    @Column
    private LocalDateTime finish;

    @Column
    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @Column
    private String cancellationReason;

    @Column
    private Double distance;

}
