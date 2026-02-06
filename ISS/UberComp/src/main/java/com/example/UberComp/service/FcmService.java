package com.example.UberComp.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FcmService {

    public void sendNotification(String fcmToken, String title, String body) {
        String type = "PANIC".equals(title) ? "panic" : "regular";

        Map<String, String> data = new HashMap<>();
        data.put("type", type);
        data.put("title", title);
        data.put("body", body);

        sendNotification(fcmToken, title, body, data);
    }

    public void sendNotification(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("FCM token is null or empty, skipping notification");
            return;
        }

        try {
            if (data == null) {
                data = new HashMap<>();
            }

            if (!data.containsKey("title")) {
                data.put("title", title);
            }
            if (!data.containsKey("body")) {
                data.put("body", body);
            }

            if (!data.containsKey("type")) {
                data.put("type", "PANIC".equals(title) ? "panic" : "regular");
            }

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .putAllData(data)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
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
                "dropoffLocation", dropoffLocation,
                "title", "New Ride Request",
                "body", "Pickup: " + pickupLocation
        );

        sendNotification(
                fcmToken,
                "New Ride Request",
                "Pickup: " + pickupLocation,
                data
        );
    }
}