package com.example.ubercorp.api;

import com.example.ubercorp.dto.GetProfileDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserApi {

    @GET("account/{id}")
    Call<GetProfileDTO> getUser(@Path("id") Long id);
}
