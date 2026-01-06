package com.example.UberComp.model;

import com.example.UberComp.dto.review.CreateReviewDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Ride ride;

    @Column
    private Double driverRating;

    @Column
    private Double vehicleRating;

    @Column
    private String comment;

    public Review (CreateReviewDTO dto, User user, Ride ride){
        this.comment = dto.getComment();
        this.driverRating = dto.getDriverRating();
        this.vehicleRating = dto.getVehicleRating();
        this.ride = ride;
        this.user = user;
    }
}
