package com.example.UberComp.dto;

import com.example.UberComp.model.Route;
import com.example.UberComp.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
public class GetRideDetailsDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Route route;
    private List<User> passengers;
    private Double price;
}
