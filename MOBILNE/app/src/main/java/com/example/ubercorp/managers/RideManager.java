package com.example.ubercorp.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.RideService;
import com.example.ubercorp.dto.CreateRideDTO;
import com.example.ubercorp.dto.GetRideDTO;
import com.example.ubercorp.dto.GetRideDetailsDTO;
import com.example.ubercorp.dto.PriceDTO;
import com.example.ubercorp.dto.RideDTO;
import com.example.ubercorp.dto.RideOrderResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;

public class RideManager {

    private final Context context;

    public RideManager(Context context) {
            this.context = context;
    }

    private String getToken () {
        SharedPreferences sharedPref = context.getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        return sharedPref.getString("auth_token", null);
    }

    public void loadDriverRides ( int page, int size, String startFrom, String startTo, Callback<GetRideDTO> callback){
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);

        Call<GetRideDTO> call =
                api.getRidesDriver("Bearer " + token, page, size, startFrom, startTo);

        call.enqueue(callback);
    }

    public void loadRideDetails(Long id, Callback<GetRideDetailsDTO> callback){
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
}
