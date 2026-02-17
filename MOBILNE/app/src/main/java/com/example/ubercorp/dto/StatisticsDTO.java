package com.example.ubercorp.dto;

import java.util.List;

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

    public StatisticsDTO() {}

    public List<DailyStatDTO> getDailyRides() {
        return dailyRides;
    }

    public void setDailyRides(List<DailyStatDTO> dailyRides) {
        this.dailyRides = dailyRides;
    }

    public List<DailyStatDTO> getDailyDistance() {
        return dailyDistance;
    }

    public void setDailyDistance(List<DailyStatDTO> dailyDistance) {
        this.dailyDistance = dailyDistance;
    }

    public List<DailyStatDTO> getDailyMoney() {
        return dailyMoney;
    }

    public void setDailyMoney(List<DailyStatDTO> dailyMoney) {
        this.dailyMoney = dailyMoney;
    }

    public int getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(int totalRides) {
        this.totalRides = totalRides;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public double getAverageRides() {
        return averageRides;
    }

    public void setAverageRides(double averageRides) {
        this.averageRides = averageRides;
    }

    public double getAverageDistance() {
        return averageDistance;
    }

    public void setAverageDistance(double averageDistance) {
        this.averageDistance = averageDistance;
    }

    public double getAverageMoney() {
        return averageMoney;
    }

    public void setAverageMoney(double averageMoney) {
        this.averageMoney = averageMoney;
    }
}