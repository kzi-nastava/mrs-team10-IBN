package com.example.ubercorp.api;
import com.example.ubercorp.dto.AppNotificationDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface NotificationService {

    @GET("api/notification")
    Call<List<AppNotificationDTO>> getNotifications(@Header("Authorization") String token);

    @POST("api/notification")
    Call<AppNotificationDTO> createNotification(
            @Header("Authorization") String token,
            @Body AppNotificationDTO notification
    );
}