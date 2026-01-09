package com.example.UberComp.dto.ride;

import com.example.UberComp.model.Route;
import lombok.Data;

@Data
public class StartRideDTO {
    private Long id;
    private Route route;
}
