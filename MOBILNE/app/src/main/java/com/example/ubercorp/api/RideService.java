package com.example.ubercorp.api;

import com.example.ubercorp.dto.CreateRideDTO;
import com.example.ubercorp.dto.GetRideDTO;
import com.example.ubercorp.dto.GetRideDetailsDTO;
import com.example.ubercorp.dto.PriceDTO;
import com.example.ubercorp.dto.RideOrderResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RideService {
    @GET("api/rides/history")
    Call<GetRideDTO> getRidesDriver(@Header("Authorization") String authToken,
                                    @Query("page") int page,
                                    @Query("size") int size,
                                    @Query("startFrom") String startFrom,
                                    @Query("startTo") String startTo);


    @GET("api/rides/{id}")
    Call<GetRideDetailsDTO> getRide(@Header("Authorization") String authToken,
                                    @Path("id") Long id);

    @POST("api/rides")
    Call<RideOrderResponseDTO> createRide(
            @Header("Authorization") String token,
            @Body CreateRideDTO rideDTO
    );

    @POST("api/rides/calculate-price")
    Call<PriceDTO> calculatePrice(
            @Header("Authorization") String token,
            @Body CreateRideDTO rideDTO
    );
}
