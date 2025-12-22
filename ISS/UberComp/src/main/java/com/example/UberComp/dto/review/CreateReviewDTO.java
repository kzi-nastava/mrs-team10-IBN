package com.example.UberComp.dto.review;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CreateReviewDTO {
    private String content;
    private Double driverRating;
    private Double vehicleRating;
    private Long userId;
    private Long rideId;
}
