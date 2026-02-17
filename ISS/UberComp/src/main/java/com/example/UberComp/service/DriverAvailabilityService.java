package com.example.UberComp.service;

import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.Driver;
import com.example.UberComp.model.Ride;
import com.example.UberComp.repository.DriverRepository;
import com.example.UberComp.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class DriverAvailabilityService {
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private RideRepository rideRepository;

    private static final int MAX_WORK_MINUTES_PER_DAY = 480;

    public void setDriverStatus(Long driverId, boolean active) {
        Driver driver = driverRepository.findById(driverId).orElseThrow();

        if (driver.getAccount() != null && driver.getAccount().getAccountStatus().equals(AccountStatus.BLOCKED)) {
            return;
        }

        resetIfNewDay(driver);

        if (active && driver.getTotalWorkMinutesToday() >= MAX_WORK_MINUTES_PER_DAY) {
            return;
        }

        if (active && driver.getStatus().equals(DriverStatus.OFFLINE)) {
            driver.setDailyWorkStart(LocalDateTime.now());
            driver.setStatus(DriverStatus.ONLINE);
        }
        else if (active && driver.getStatus().equals(DriverStatus.OFFLINE_AFTER_RIDE)) {
            driver.setDailyWorkStart(LocalDateTime.now());
            driver.setStatus(DriverStatus.DRIVING);
        }
        else if (!active && driver.getStatus().equals(DriverStatus.ONLINE)) {
            updateWorkMinutes(driver);
            driver.setStatus(DriverStatus.OFFLINE);
            driver.setDailyWorkStart(null);
        } else if (!active && driver.getStatus().equals(DriverStatus.DRIVING)) {
            Ride ongoing = rideRepository.findByStatusAndDriverId(RideStatus.Ongoing, driverId);
            updateWorkMinutes(driver, ongoing.getEstimatedTimeArrival());
            driver.setStatus(DriverStatus.OFFLINE_AFTER_RIDE);
            driver.setDailyWorkStart(null);
        }

        driverRepository.save(driver);
    }

    private void resetIfNewDay(Driver driver) {
        LocalDate today = LocalDate.now();

        if (driver.getLastActivityDate() == null ||
                !driver.getLastActivityDate().equals(today)) {

            driver.setTotalWorkMinutesToday(0);
            driver.setLastActivityDate(today);
            driver.setDailyWorkStart(null);
        }
    }

    private void updateWorkMinutes(Driver driver, LocalDateTime time){
        if (driver.getDailyWorkStart() != null) {
            long minutesWorked = ChronoUnit.MINUTES.between(
                    driver.getDailyWorkStart(),
                    time
            );

            driver.setTotalWorkMinutesToday(
                    driver.getTotalWorkMinutesToday() + (int) minutesWorked
            );
        }
    }

    private void updateWorkMinutes(Driver driver) {
        if (driver.getDailyWorkStart() != null) {
            long minutesWorked = ChronoUnit.MINUTES.between(
                    driver.getDailyWorkStart(),
                    LocalDateTime.now()
            );

            driver.setTotalWorkMinutesToday(
                    driver.getTotalWorkMinutesToday() + (int) minutesWorked
            );
        }
    }

    public int getWorkMinutes(Long driverId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow();
        resetIfNewDay(driver);

        int workedSoFar = driver.getTotalWorkMinutesToday();

        if (driver.getStatus().equals(DriverStatus.ONLINE) && driver.getDailyWorkStart() != null) {
            long currentSessionMinutes = ChronoUnit.MINUTES.between(
                    driver.getDailyWorkStart(),
                    LocalDateTime.now()
            );
            workedSoFar += currentSessionMinutes;
        }

        return Math.max(0,  workedSoFar);
    }
}
