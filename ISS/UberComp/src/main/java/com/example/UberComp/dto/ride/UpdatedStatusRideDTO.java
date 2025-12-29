package com.example.UberComp.dto.ride;

import com.example.UberComp.enums.RideStatus;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedStatusRideDTO {
    private Long id;
    private RideStatus rideStatus;
}
