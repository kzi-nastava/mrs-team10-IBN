package com.example.ubercorp.api;

import com.example.ubercorp.dto.CancelRideDTO;
import com.example.ubercorp.dto.CreateRideDTO;
import com.example.ubercorp.dto.FavoriteRouteDTO;
import com.example.ubercorp.dto.FinishedRideDTO;
import com.example.ubercorp.dto.GetComplaintDTO;
import com.example.ubercorp.dto.GetReviewDTO;
import com.example.ubercorp.dto.GetRideDTO;
import com.example.ubercorp.dto.GetRideDetailsDTO;
import com.example.ubercorp.dto.IncomingRideDTO;
import com.example.ubercorp.dto.PriceDTO;
import com.example.ubercorp.dto.RideDTO;
import com.example.ubercorp.dto.RideMomentDTO;
import com.example.ubercorp.dto.RideOrderResponseDTO;
import com.example.ubercorp.dto.StopRideDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RideService {
    @GET("api/rides/history")
    Call<GetRideDTO> getRideHistory(@Header("Authorization") String authToken,
                                    @Query("page") int page,
                                    @Query("size") int size,
                                    @Query("startFrom") String startFrom,
                                    @Query("startTo") String startTo,
                                    @Query("sort") String sort);


    @GET("api/rides/{id}")
    Call<GetRideDetailsDTO> getRide(@Header("Authorization") String authToken,
                                    @Path("id") Long id);

    @GET("api/rides/tracking/{token}")
    Call<GetRideDetailsDTO> getRideByToken(@Header("Authorization") String authToken,
                                           @Path("token") String rideToken);

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

    @GET("api/rides/favorites")
    Call<List<FavoriteRouteDTO>> getFavorites(
            @Header("Authorization") String token
    );

    @PUT("api/rides/history/{id}/add-to-favorites")
    Call<Void> addToFavorites(
            @Header("Authorization") String token,
            @Path("id") Long historyId
    );

    @DELETE("api/rides/history/by-route-id/{routeId}")
    Call<Void> removeByRouteId(
            @Header("Authorization") String token,
            @Path("routeId") Long routeId
    );

    @GET("api/rides/incoming")
    Call<IncomingRideDTO> getIncomingRide(@Header("Authorization") String token);

    @PUT("api/rides/cancel")
    Call<Void> cancelRide(@Header("Authorization") String token, @Body CancelRideDTO cancelledRide);

    @PUT("api/rides/{id}/start")
    Call<Void> startRide(@Header("Authorization") String token, @Path("id") Long id, @Body RideMomentDTO start);

    @GET("api/reviews/{id}")
    Call<List<GetReviewDTO>> getReviews(@Header("Authorization") String token, @Path("id") Long id);

    @GET("api/reports/{id}")
    Call<List<GetComplaintDTO>> getComplaints(@Header("Authorization") String token, @Path("id") Long id);

    @GET("api/rides/activeRides")
    Call<List<RideDTO>> getActiveRides();

    @PUT("api/rides/finish/{id}")
    Call<FinishedRideDTO> finishRide(@Header("Authorization") String token, @Path("id") Long id, @Body RideMomentDTO finish);

    @PUT("api/rides/stop")
    Call<FinishedRideDTO> stopRide(@Header("Authorization") String token, @Body StopRideDTO ride);

    @PUT("api/rides/panic")
    Call<FinishedRideDTO> panic(@Header("Authorization") String token, @Body StopRideDTO ride);

    @GET("api/rides/ongoing")
    Call<Boolean> getOngoingRide(@Header("Authorization") String token);
}
