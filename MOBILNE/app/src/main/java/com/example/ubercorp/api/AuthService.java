package com.example.ubercorp.api;

import com.example.ubercorp.dto.AccountDTO;
import com.example.ubercorp.dto.GetProfileDTO;
import com.example.ubercorp.dto.RegisterDTO;
import com.example.ubercorp.dto.SetPasswordDTO;
import com.example.ubercorp.model.AuthToken;
import com.example.ubercorp.model.Credentials;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @POST("auth/login")
    Call<AuthToken> login(@Body Credentials creds);

    @POST("auth/register")
    Call<GetProfileDTO> register(@Body RegisterDTO register);

    @POST("auth/forgot-password")
    Call<Void> forgotPassword(@Body AccountDTO account);

    @GET("auth/verify/{verifyID}")
    Call<Void> verifyAccount(@Path("verifyID") String verifyID);

    @GET("auth/set-password/{token}")
    Call<Void> checkPasswordToken(@Path("token") String token);

    @POST("auth/set-password/{token}")
    Call<Void> setPassword(@Path("token") String token, @Body SetPasswordDTO password);
}