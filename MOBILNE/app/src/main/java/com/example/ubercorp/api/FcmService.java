package com.example.ubercorp.api;

import com.example.ubercorp.dto.FcmTokenDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FcmService {

    @POST("api/fcm/token")
    Call<Void> updateFcmToken(
            @Header("Authorization") String token,
            @Body FcmTokenDTO fcmToken
    );
}