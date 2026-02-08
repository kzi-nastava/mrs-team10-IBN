package com.example.UberComp.service;

import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.model.ScheduledRide;
import com.example.UberComp.repository.DriverRepository;
import com.example.UberComp.repository.ScheduledRideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RideReminderScheduler {

    @Autowired
    private ScheduledRideRepository scheduledRideRepository;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private DriverRepository driverRepository;

    @Scheduled(cron = "*/20 * * * * *")
    public void checkForReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledRide> scheduledRides = scheduledRideRepository.findPendingScheduledRides(now);

        for (ScheduledRide ride : scheduledRides) {
            LocalDateTime scheduled = ride.getScheduled();
            long secondsUntilRide = ChronoUnit.SECONDS.between(now, scheduled);

            checkAndSend(ride, secondsUntilRide, 15 * 60, 20, ride.getReminder15Sent(), 15L);
            checkAndSend(ride, secondsUntilRide, 10 * 60, 20, ride.getReminder10Sent(), 10L);
            checkAndSend(ride, secondsUntilRide, 5 * 60, 20, ride.getReminder5Sent(), 5L);

            long seconds = ChronoUnit.SECONDS.between(now, ride.getDriverShouldLeaveAt());

            if (!ride.getDriverNotified() && Math.abs(seconds) <= 30) {
                updateDriverNotificationStatus(ride);
                notificationService.sendSignalToFrontend(ride.getDriver());
            }
        }
    }

    @Transactional
    protected void updateDriverNotificationStatus(ScheduledRide ride) {
        ride.setDriverNotified(true);
        scheduledRideRepository.save(ride);
        ride.getDriver().setStatus(DriverStatus.DRIVING);
        driverRepository.save(ride.getDriver());
    }

    @Transactional
    protected void checkAndSend(
            ScheduledRide ride,
            long secondsUntilRide,
            int targetSeconds,
            int toleranceSeconds,
            boolean alreadySent,
            long minutesLabel
    ) {
        if (!alreadySent &&
                secondsUntilRide >= targetSeconds - toleranceSeconds &&
                secondsUntilRide <= targetSeconds + toleranceSeconds) {

            switch ((int) minutesLabel) {
                case 15 -> ride.setReminder15Sent(true);
                case 10 -> ride.setReminder10Sent(true);
                case 5 -> ride.setReminder5Sent(true);
            }
            scheduledRideRepository.saveAndFlush(ride);
            notificationService.sendRideReminder(ride, minutesLabel);
        }
    }
}