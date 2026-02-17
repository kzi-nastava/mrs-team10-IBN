package com.example.ubercorp.api;

import com.example.ubercorp.dto.StatisticsDTO;
import com.example.ubercorp.dto.UserBasicDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StatisticsService {
    @GET("api/statistics/my")
    Call<StatisticsDTO> getMyStatistics(
            @Header("Authorization") String token,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("api/statistics/all/{userType}")
    Call<StatisticsDTO> getAllUsersStatistics(
            @Header("Authorization") String token,
            @Path("userType") String userType,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("api/statistics/user/{userId}")
    Call<StatisticsDTO> getUserStatistics(
            @Header("Authorization") String token,
            @Path("userId") Long userId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("api/statistics/users/drivers")
    Call<List<UserBasicDTO>> getAllDrivers(@Header("Authorization") String token);

    @GET("api/statistics/users/passengers")
    Call<List<UserBasicDTO>> getAllPassengers(@Header("Authorization") String token);
}
