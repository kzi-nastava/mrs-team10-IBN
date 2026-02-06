package com.example.ubercorp.api;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.ubercorp.R;
import com.example.ubercorp.activities.HomeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "uber_fcm_notifications";
    private static final String PANIC_ID = "uber_fcm_notifications_panic";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM token: " + token);
        sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        createNotificationChannels();

        if (!remoteMessage.getData().isEmpty()) {
            Map<String, String> data = remoteMessage.getData();

            String type = data.get("type");
            String title = data.get("title");
            String body = data.get("body");

            if ("panic".equalsIgnoreCase(type)) {
                showNotification(title, body, true);
            } else if ("ride".equals(type)) {
                handleRideNotification(data);
            } else {
                showNotification(title, body, false);
            }
        }
        else if (remoteMessage.getNotification() != null) {
            showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    false
            );
        }

        Log.d(TAG, "=== End FCM Message ===");
    }

    private void handleRideNotification(Map<String, String> data) {
        String title = data.getOrDefault("title", "New Ride Request");
        String body = data.getOrDefault("body", "You have a new ride");

        showNotification(title, body, false);
    }

    private void showNotification(String title, String message, boolean isPanic) {
        Log.d(TAG, "showNotification called:");
        Log.d(TAG, "  Title: " + title);
        Log.d(TAG, "  Message: " + message);
        Log.d(TAG, "  isPanic: " + isPanic);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        String channelId = isPanic ? PANIC_ID : CHANNEL_ID;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.standard)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 250, 500});

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }


        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        Uri normalSound = Uri.parse(
                "android.resource://" + getPackageName() + "/" + R.raw.youve_been_informed_345
        );


        NotificationChannel normalChannel = new NotificationChannel(
                CHANNEL_ID,
                "Uber Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        normalChannel.setDescription("Regular notifications");
        normalChannel.enableVibration(true);
        normalChannel.enableLights(true);
        normalChannel.setShowBadge(true);
        normalChannel.setSound(normalSound, audioAttributes);
        normalChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        Uri panicSound = Uri.parse(
                "android.resource://" + getPackageName() + "/" + R.raw.attention_required_127
        );

        NotificationChannel panicChannel = new NotificationChannel(
                PANIC_ID,
                "Uber Panic Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        panicChannel.setDescription("Emergency notifications");
        panicChannel.enableVibration(true);
        panicChannel.enableLights(true);
        panicChannel.setShowBadge(true);
        panicChannel.setSound(panicSound, audioAttributes);
        panicChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(normalChannel);
            manager.createNotificationChannel(panicChannel);
        }
    }

    private void sendTokenToServer(String token) {
        android.content.SharedPreferences sharedPref =
                getSharedPreferences("uber_corp", MODE_PRIVATE);

        String authToken = sharedPref.getString("auth_token", null);
        if (authToken == null) return;

        com.example.ubercorp.api.ApiClient apiClient =
                com.example.ubercorp.api.ApiClient.getInstance();

        com.example.ubercorp.api.FcmService fcmService =
                apiClient.createService(com.example.ubercorp.api.FcmService.class);

        com.example.ubercorp.dto.FcmTokenDTO dto =
                new com.example.ubercorp.dto.FcmTokenDTO(token);

        retrofit2.Call<Void> call =
                fcmService.updateFcmToken("Bearer " + authToken, dto);

        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call,
                                   retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "FCM token sent successfully");
                } else {
                    Log.e(TAG, "Failed to send FCM token: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Log.e(TAG, "Error sending FCM token", t);
            }
        });
    }
}