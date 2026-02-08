package com.example.ubercorp.api;

import com.example.ubercorp.dto.AccountDTO;
import com.example.ubercorp.dto.GetProfileDTO;
import com.example.ubercorp.dto.RegisterDTO;
import com.example.ubercorp.model.AuthToken;
import com.example.ubercorp.model.Credentials;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

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
}