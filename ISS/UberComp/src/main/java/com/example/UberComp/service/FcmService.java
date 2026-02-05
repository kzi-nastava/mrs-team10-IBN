package com.example.UberComp.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class FcmService {

    public void sendNotification(String fcmToken, String title, String body) {
        sendNotification(fcmToken, title, body, null);
    }

    public void sendNotification(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("FCM token is null or empty, skipping notification");
            return;
        }

        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            log.info("Successfully sent FCM message: {}", response);
        } catch (Exception e) {
            log.error("Failed to send FCM notification to token: {}", fcmToken, e);
        }
    }

    public void sendRideNotification(String fcmToken, Long rideId, String pickupLocation, String dropoffLocation) {
        Map<String, String> data = Map.of(
                "type", "ride",
                "rideId", String.valueOf(rideId),
                "pickupLocation", pickupLocation,
                "dropoffLocation", dropoffLocation
        );

        sendNotification(
                fcmToken,
                "New Ride Request",
                "Pickup: " + pickupLocation,
                data
        );
    }
}