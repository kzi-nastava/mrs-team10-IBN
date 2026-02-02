package com.example.ubercorp.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.RideService;
import com.example.ubercorp.dto.CancelRideDTO;
import com.example.ubercorp.dto.CreateRideDTO;
import com.example.ubercorp.dto.FavoriteRouteDTO;
import com.example.ubercorp.dto.GetComplaintDTO;
import com.example.ubercorp.dto.GetReviewDTO;
import com.example.ubercorp.dto.GetRideDTO;
import com.example.ubercorp.dto.GetRideDetailsDTO;
import com.example.ubercorp.dto.IncomingRideDTO;
import com.example.ubercorp.dto.PriceDTO;
import com.example.ubercorp.dto.RideOrderResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class RideManager {

    private final Context context;

    public RideManager(Context context) {
        this.context = context;
    }

    private String getToken() {
        SharedPreferences sharedPref = context.getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        return sharedPref.getString("auth_token", null);
    }

    public void loadDriverRides(int page, int size, String startFrom, String startTo, Callback<GetRideDTO> callback) {
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);
        Call<GetRideDTO> call = api.getRidesDriver("Bearer " + token, page, size, startFrom, startTo);
        call.enqueue(callback);
    }

    public void loadRideDetails(Long id, Callback<GetRideDetailsDTO> callback) {
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);
        Call<GetRideDetailsDTO> call = api.getRide("Bearer " + token, id);
        call.enqueue(callback);
    }

    public void createRide(CreateRideDTO rideDTO, Callback<RideOrderResponseDTO> callback) {
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);
        Call<RideOrderResponseDTO> call = api.createRide("Bearer " + token, rideDTO);
        call.enqueue(callback);
    }

    public void calculatePrice(CreateRideDTO rideDTO, Callback<PriceDTO> callback) {
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);
        Call<PriceDTO> call = api.calculatePrice("Bearer " + token, rideDTO);
        call.enqueue(callback);
    }

    public void getFavoriteRoutes(Callback<List<FavoriteRouteDTO>> callback) {
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);
        Call<List<FavoriteRouteDTO>> call = api.getFavorites("Bearer " + token);
        call.enqueue(callback);
    }

    public void addToFavorites(Long historyId, Callback<Void> callback) {
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);
        Call<Void> call = api.addToFavorites("Bearer " + token, historyId);
        call.enqueue(callback);
    }

    public void removeFromFavoritesByRouteId(Long routeId, Callback<Void> callback) {
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);
        Call<Void> call = api.removeByRouteId("Bearer " + token, routeId);
        call.enqueue(callback);
    }

    public void getIncomingRide(Callback<IncomingRideDTO> callback){
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);
        Call<IncomingRideDTO> call = api.getIncomingRide("Bearer " + token);
        call.enqueue(callback);
    }

    public void cancelRide(Long rideID, String reason, boolean cancelledByDriver, Callback<Void> callback){
        String token = getToken();
        if (token == null) return;

        CancelRideDTO cancelRide = new CancelRideDTO(rideID, reason, cancelledByDriver);
        RideService api = ApiClient.getInstance().createService(RideService.class);
        Call<Void> call = api.cancelRide("Bearer " + token, cancelRide);
        call.enqueue(callback);
    }

    public void getReviews(Long rideID, Callback<List<GetReviewDTO>> callback){
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);
        Call<List<GetReviewDTO>> call = api.getReviews("Bearer " + token, rideID);
        call.enqueue(callback);
    }

    public void getComplaints(Long rideID, Callback<List<GetComplaintDTO>> callback){
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);
        Call<List<GetComplaintDTO>> call = api.getComplaints("Bearer " + token, rideID);
        call.enqueue(callback);
    }
}