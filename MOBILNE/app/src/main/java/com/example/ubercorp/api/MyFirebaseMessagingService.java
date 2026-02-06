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

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "uber_fcm_notifications";
    private static final String PANIC_ID = "Uber_fcm_notifications_panic";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM token: " + token);

        sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            showNotification(title, body);
        }

        if (remoteMessage.getData().size() > 0) {

            String type = remoteMessage.getData().get("type");

            if ("ride".equals(type)) {
                handleRideNotification(remoteMessage.getData());
            } else {
                if (remoteMessage.getNotification() == null) {
                    String title = remoteMessage.getData().get("title");
                    String content = remoteMessage.getData().get("content");
                    showNotification(title, content);
                }
            }
        }
    }

    private void handleRideNotification(java.util.Map<String, String> data) {
        String title = data.get("title");
        String body = data.get("body");
        String pickupLocation = data.get("pickupLocation");

        showNotification(title != null ? title : "New Ride Request",
                body != null ? body : "Pickup: " + pickupLocation);
    }

    private void handleRegularNotification(java.util.Map<String, String> data) {
        String title = data.get("title");
        String content = data.get("content");

        showNotification(title, content);
    }

    private void showNotification(String title, String message) {
        createNotificationChannel();

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        String channelId = CHANNEL_ID;
        if(title.equals("PANIC")) channelId = PANIC_ID;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PANIC_ID)
                .setSmallIcon(R.drawable.ic_car)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 250, 500});
                //.setDefaults(NotificationCompat.DEFAULT_SOUND);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Uber FCM Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Push notifications for rides and updates");
            channel.enableVibration(true);
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.youve_been_informed_345);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            channel.setSound(sound, audioAttributes);

            NotificationChannel panic = new NotificationChannel(
                    PANIC_ID,
                    "Uber FCM Panic Notifications",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            panic.setDescription("Notifications for emergencies");
            panic.enableVibration(true);
            panic.setShowBadge(true);
            channel.enableLights(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            Uri panicSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.attention_required_127);
            panic.setSound(panicSound, audioAttributes);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                manager.createNotificationChannel(panic);
            }
        }
    }

    private void sendTokenToServer(String token) {
        android.content.SharedPreferences sharedPref =
                getSharedPreferences("uber_corp", MODE_PRIVATE);
        String authToken = sharedPref.getString("auth_token", null);

        if (authToken != null) {
            com.example.ubercorp.api.ApiClient apiClient =
                    com.example.ubercorp.api.ApiClient.getInstance();
            com.example.ubercorp.api.FcmService fcmService =
                    apiClient.createService(com.example.ubercorp.api.FcmService.class);

            com.example.ubercorp.dto.FcmTokenDTO dto =
                    new com.example.ubercorp.dto.FcmTokenDTO(token);

            retrofit2.Call<Void> call = fcmService.updateFcmToken("Bearer " + authToken, dto);
            call.enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "FCM token sent to server successfully");
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
}