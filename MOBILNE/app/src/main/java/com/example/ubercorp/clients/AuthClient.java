package com.example.ubercorp.clients;

import com.example.ubercorp.BuildConfig;
import com.example.ubercorp.model.AuthToken;
import com.example.ubercorp.model.Credentials;
import com.google.gson.internal.GsonBuildConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthClient {
    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.API_HOST + "auth/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient())
            .build();


    public static AuthService authService = retrofit.create(AuthService.class);
}
