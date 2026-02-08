package com.example.UberComp.dto.review;

import com.example.UberComp.model.Review;
import lombok.Data;

@Data
public class GetReviewDTO {
    private Long id;
    private String username;
    private String comment;
    private Double driverRating;
    private Double vehicleRating;

    public GetReviewDTO(Review review){
        this.id = review.getId();
        this.username = review.getUser().getName() + " " + review.getUser().getLastName();
        this.comment = review.getComment();
        this.driverRating = review.getDriverRating();
        this.vehicleRating = review.getVehicleRating();
    }
}
