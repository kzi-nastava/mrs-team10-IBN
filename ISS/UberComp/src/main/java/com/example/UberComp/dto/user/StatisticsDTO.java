package com.example.UberComp.dto.user;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDTO {
    private List<DailyStatDTO> dailyRides;
    private List<DailyStatDTO> dailyDistance;
    private List<DailyStatDTO> dailyMoney;

    private int totalRides;
    private double totalDistance;
    private double totalMoney;

    private double averageRides;
    private double averageDistance;
    private double averageMoney;
}