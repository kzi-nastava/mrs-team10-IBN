package com.example.UberComp.service;

import com.example.UberComp.dto.NotificationDTO;
import com.example.UberComp.dto.ride.IncomingRideDTO;
import com.example.UberComp.enums.AccountType;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.NotificationRepository;
import com.example.UberComp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Notification broadcastToAdmins(NotificationDTO rawNotif) {
        Notification notification = new Notification(rawNotif.getTitle(), rawNotif.getContent(), LocalDateTime.now());
        List<User> admins = userRepository.findAllByRole(AccountType.ADMINISTRATOR);
        notification.setNotifiedUsers(admins);
        notificationRepository.save(notification);
        return notification;
    }

    public void sendToAllAdmins(Notification notification) {
        List<User> admins = userRepository.findAllByRole(AccountType.ADMINISTRATOR);
        for (User admin : admins) {
            sendToUser(admin.getAccount().getEmail(), notification);
        }
    }

    public void sendToUser(String userEmail, Notification notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/notifications",
                    notification
            );
        } catch (Exception e) {
            System.err.println("Failed to send WebSocket to " + userEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Notification> getNotifsForUser(User user) {
        List<Notification> notifs = notificationRepository.findByNotifiedUsersIdOrderByTime(user.getId());
        return notifs;
    }

    @Transactional
    public void sendRideReminder(ScheduledRide ride, Long minutesBefore) {
        String title = "⏰ Ride reminder";
        String content = String.format("Your ride starts in %d minutes", minutesBefore);

        for (User passenger : ride.getPassengers()) {
            Notification notification = new Notification(title, content, LocalDateTime.now());
            notification.setNotifiedUsers(List.of(passenger));

            notificationRepository.save(notification);
            notificationRepository.flush();
            sendToUser(passenger.getAccount().getEmail(), notification);
        }
    }

    @Transactional
    public void sendSignalToFrontend(Driver driver) {
        try {
            String email = driver.getAccount().getEmail();
            messagingTemplate.convertAndSend(
                    "/topic/ride/" + email,
                    Map.of("type", "INCOMING_RIDE")
            );

        } catch (Exception e) {
            System.err.println("FAILED TO SEND RIDE SIGNAL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendScheduledRideNotifications(ScheduledRide ride, User mainPassenger) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String scheduledTime = ride.getScheduled().format(formatter);

        String passengerTitle = "✅ Ride Scheduled Successfully";
        String passengerContent = String.format(
                "Your ride has been scheduled for %s.\n" +
                        "Driver: %s\n" +
                        "Vehicle: %s\n" +
                        "Price: %.2f RSD",
                scheduledTime,
                ride.getDriver().getName(),
                ride.getDriver().getVehicle().getModel(),
                ride.getPrice()
        );

        Notification passengerNotif = new Notification(passengerTitle, passengerContent, LocalDateTime.now());
        passengerNotif.setNotifiedUsers(List.of(mainPassenger));
        notificationRepository.save(passengerNotif);

        sendToUser(mainPassenger.getAccount().getEmail(), passengerNotif);

        String driverTitle = "🚗 New Scheduled Ride";
        String driverContent = String.format(
                "You have a new scheduled ride for %s.\n" +
                        "Passenger: %s\n" +
                        "Pickup: %s\n" +
                        "Destination: %s\n" +
                        "Price: %.2f RSD",
                scheduledTime,
                mainPassenger.getName(),
                ride.getRoute().getStations().get(0).getAddress(),
                ride.getRoute().getStations().get(ride.getRoute().getStations().size() - 1).getAddress(),
                ride.getPrice()
        );

        Notification driverNotif = new Notification(driverTitle, driverContent, LocalDateTime.now());
        driverNotif.setNotifiedUsers(List.of(ride.getDriver()));
        notificationRepository.save(driverNotif);

        sendToUser(ride.getDriver().getAccount().getEmail(), driverNotif);
    }

    public void sendImmediateRideNotifications(Ride ride, User mainPassenger, Long estimatedPickupMinutes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String estimatedArrival = ride.getEstimatedTimeArrival().format(formatter);

        String passengerTitle = "✅ Ride Ordered Successfully";
        String passengerContent = String.format(
                "Your ride has been confirmed!\n" +
                        "Driver: %s\n" +
                        "Phone: %s\n" +
                        "Vehicle: %s\n" +
                        "Arrives in: %d min\n" +
                        "Price: %.2f RSD",
                ride.getDriver().getName(),
                ride.getDriver().getPhone(),
                ride.getDriver().getVehicle().getModel(),
                estimatedPickupMinutes,
                ride.getPrice()
        );

        Notification passengerNotif = new Notification(passengerTitle, passengerContent, LocalDateTime.now());
        passengerNotif.setNotifiedUsers(List.of(mainPassenger));
        notificationRepository.save(passengerNotif);

        sendToUser(mainPassenger.getAccount().getEmail(), passengerNotif);

        sendToLinkedPassengers(ride, mainPassenger, estimatedPickupMinutes);

        String driverTitle = "🚗 New Ride Request";
        String driverContent = String.format(
                "You have a new ride!\n" +
                        "Passenger: %s\n" +
                        "Phone: %s\n" +
                        "Pickup: %s\n" +
                        "Destination: %s\n" +
                        "Estimated arrival: %s\n" +
                        "Price: %.2f RSD",
                mainPassenger.getName(),
                mainPassenger.getPhone(),
                ride.getRoute().getStations().get(0).getAddress(),
                ride.getRoute().getStations().get(ride.getRoute().getStations().size() - 1).getAddress(),
                estimatedArrival,
                ride.getPrice()
        );

        Notification driverNotif = new Notification(driverTitle, driverContent, LocalDateTime.now());
        driverNotif.setNotifiedUsers(List.of(ride.getDriver()));
        notificationRepository.save(driverNotif);

        sendToUser(ride.getDriver().getAccount().getEmail(), driverNotif);
        sendSignalToFrontend(ride.getDriver());
    }

    public void sendToLinkedPassengers(Ride ride, User mainPassenger, Long estimatedPickupMinutes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        String passengerTitle = "Added to a ride";
        String passengerContent = String.format(
                "You are added to a ride!\n" +
                        "Driver: %s\n" +
                        "Vehicle: %s\n" +
                        "Arrives in: %d min\n" +
                        "Price: %.2f RSD",
                ride.getDriver().getName(),
                ride.getDriver().getVehicle().getModel(),
                estimatedPickupMinutes,
                ride.getPrice()
        );

        Notification passengerNotif = new Notification(passengerTitle, passengerContent, LocalDateTime.now());
        List<User> notifiedUsers = new ArrayList<>();
        for (User user: ride.getPassengers()) {
            if (user.getAccount().getEmail() != mainPassenger.getAccount().getEmail()) {
                notifiedUsers.add(user);
                sendToUser(user.getAccount().getEmail(), passengerNotif);
            }
        }
        passengerNotif.setNotifiedUsers(notifiedUsers);
        notificationRepository.save(passengerNotif);

    }
}