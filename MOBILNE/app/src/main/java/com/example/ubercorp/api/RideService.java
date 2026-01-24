package com.example.ubercorp.api;

import com.example.ubercorp.dto.GetRideDTO;
import com.example.ubercorp.dto.GetRideDetailsDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RideService {
    @GET("api/rides/driver")
    Call<GetRideDTO> getRidesDriver(@Header("Authorization") String authToken,
                                    @Query("page") int page,
                                    @Query("size") int size);

    @GET("api/rides/{id}")
    Call<GetRideDetailsDTO> getRide(@Header("Authorization") String authToken,
                                    @Path("id") Long id);
}
