package com.example.ubercorp.api;

import com.example.ubercorp.dto.DriverDTO;
import com.example.ubercorp.dto.UpdateDriverDTO;
import com.example.ubercorp.dto.CreateDriverDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DriverService {

    @GET("api/drivers/me")
    Call<DriverDTO> getDriverProfile(@Header("Authorization") String authToken);

    @POST("api/drivers")
    Call<DriverDTO> register(@Header("Authorization") String authToken, @Body CreateDriverDTO createDriverDTO);

    @POST("api/drivers/me/change-request")
    Call<Void> submitDriverChangeRequest(@Header("Authorization") String authToken, @Body UpdateDriverDTO changeRequest);
}