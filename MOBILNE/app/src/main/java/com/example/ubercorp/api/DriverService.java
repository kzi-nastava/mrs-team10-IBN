package com.example.ubercorp.api;

import com.example.ubercorp.dto.DriverDTO;
import com.example.ubercorp.dto.UpdateDriverDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DriverService {

    @GET("drivers/me")
    Call<DriverDTO> getDriverProfile();

    @POST("drivers/me/change-request")
    Call<Void> submitDriverChangeRequest(@Body UpdateDriverDTO changeRequest);
}