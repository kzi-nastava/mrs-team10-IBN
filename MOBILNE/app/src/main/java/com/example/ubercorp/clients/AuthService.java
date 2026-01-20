package com.example.ubercorp.clients;

import com.example.ubercorp.model.AuthToken;
import com.example.ubercorp.model.Credentials;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("login")
    Call<AuthToken> login(@Body Credentials creds);
}
