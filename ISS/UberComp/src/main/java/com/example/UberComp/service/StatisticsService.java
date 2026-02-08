package com.example.UberComp.service;

import com.example.UberComp.dto.user.DailyStatDTO;
import com.example.UberComp.dto.user.StatisticsDTO;
import com.example.UberComp.dto.user.UserBasicDTO;
import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    public StatisticsDTO getUserStatisticsByEmail(String email, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return getUserStatisticsById(user.getId(), startDate, endDate);
    }

    public StatisticsDTO getUserStatisticsById(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Ride> rides;
        boolean isDriver = false;

        Optional<Driver> driverOpt = driverRepository.findById(userId);
        if (driverOpt.isPresent()) {
            rides = rideRepository.findFinishedRidesByDriverAndDateRange(
                    driverOpt.get().getId(), start, end);
            isDriver = true;
        } else {
            rides = rideRepository.findFinishedRidesByPassengerAndDateRange(
                    user.getId(), start, end);
        }

        return calculateStatistics(rides, startDate, endDate, isDriver);
    }
    public StatisticsDTO getAllUsersStatistics(String userType, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Ride> rides;
        boolean isDriver = userType.equals("drivers");

        if (isDriver) {
            rides = rideRepository.findFinishedDriverRidesByDateRange(start, end);
        } else {
            rides = rideRepository.findFinishedPassengerRidesByDateRange(start, end);
        }

        return calculateStatistics(rides, startDate, endDate, isDriver);
    }

    public List<UserBasicDTO> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        return drivers.stream()
                .filter(driver -> driver.getAccount().getAccountStatus().equals(AccountStatus.VERIFIED))
                .map(driver -> new UserBasicDTO(
                        driver.getId(),
                        driver.getName(), driver.getLastName(), driver.getAccount().getEmail()
                ))
                .collect(Collectors.toList());
    }

    public List<UserBasicDTO> getAllPassengers() {
        List<User> allUsers = userRepository.findAll();
        List<Long> driverUserIds = driverRepository.findAll().stream()
                .map(Driver::getId)
                .toList();

        return allUsers.stream()
                .filter(user -> !driverUserIds.contains(user.getId()))
                .filter(user -> !user.getAccount().getAccountType().equals(AccountType.ADMINISTRATOR))
                .filter(user -> user.getAccount().getAccountStatus().equals(AccountStatus.VERIFIED))
                .map(user -> new UserBasicDTO(
                        user.getId(),
                        user.getName(), user.getLastName(), user.getAccount().getEmail()
                ))
                .collect(Collectors.toList());
    }

    private StatisticsDTO calculateStatistics(List<Ride> rides, LocalDate startDate,
                                              LocalDate endDate, boolean isDriver) {

        Map<LocalDate, DailyData> dailyDataMap = new TreeMap<>();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dailyDataMap.put(current, new DailyData());
            current = current.plusDays(1);
        }

        for (Ride ride : rides) {
            if (ride.getStatus() != RideStatus.Finished) {
                continue;
            }

            LocalDate rideDate = ride.getStart().toLocalDate();

            if (!rideDate.isBefore(startDate) && !rideDate.isAfter(endDate)) {
                DailyData data = dailyDataMap.get(rideDate);
                data.rideCount++;
                data.distance += ride.getDistance();

                if (isDriver) {
                    data.money += ride.getPrice();
                } else {
                    int passengerCount = ride.getPassengers().size();
                    if (passengerCount > 0) {
                        data.money += ride.getPrice() / passengerCount;
                    }
                }
            }
        }

        List<DailyStatDTO> dailyRides = new ArrayList<>();
        List<DailyStatDTO> dailyDistance = new ArrayList<>();
        List<DailyStatDTO> dailyMoney = new ArrayList<>();

        double totalRides = 0;
        double totalDistance = 0;
        double totalMoney = 0;

        for (Map.Entry<LocalDate, DailyData> entry : dailyDataMap.entrySet()) {
            String dateStr = entry.getKey().toString();
            DailyData data = entry.getValue();

            dailyRides.add(new DailyStatDTO(dateStr, data.rideCount));
            dailyDistance.add(new DailyStatDTO(dateStr, data.distance));
            dailyMoney.add(new DailyStatDTO(dateStr, data.money));

            totalRides += data.rideCount;
            totalDistance += data.distance;
            totalMoney += data.money;
        }

        long dayCount = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double avgRides = totalRides / dayCount;
        double avgDistance = totalDistance / dayCount;
        double avgMoney = totalMoney / dayCount;

        StatisticsDTO dto = new StatisticsDTO();
        dto.setDailyRides(dailyRides);
        dto.setDailyDistance(dailyDistance);
        dto.setDailyMoney(dailyMoney);
        dto.setTotalRides((int) totalRides);
        dto.setTotalDistance(totalDistance);
        dto.setTotalMoney(totalMoney);
        dto.setAverageRides(avgRides);
        dto.setAverageDistance(avgDistance);
        dto.setAverageMoney(avgMoney);

        return dto;
    }

    private static class DailyData {
        int rideCount = 0;
        double distance = 0.0;
        double money = 0.0;
    }
}