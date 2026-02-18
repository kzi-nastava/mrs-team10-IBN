package com.example.UberComp.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CreateReviewDTO {
    private String comment;
    @Min(value = 0, message = "Rate number must be at least 0")
    @Max(value = 5, message = "Rate number must be at most 5")
    private Double driverRating;
    @Min(value = 0, message = "Rate number must be at least 0")
    @Max(value = 5, message = "Rate number must be at most 5")
    private Double vehicleRating;
    private Long userId;
    private Long rideId;

}
