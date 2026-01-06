package com.example.UberComp.dto.review;

import com.example.UberComp.model.Review;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CreatedReviewDTO {
    private Long id;
    private String comment;
    private Double driverRating;
    private Double vehicleRating;
    private Long userId;
    private Long rideId;

    public CreatedReviewDTO(Review review){
        this.id = review.getId();
        this.comment = review.getComment();
        this.driverRating = review.getDriverRating();
        this.vehicleRating = review.getVehicleRating();
        this.rideId = review.getRide().getId();
        this.userId = review.getUser().getId();
    }
}
