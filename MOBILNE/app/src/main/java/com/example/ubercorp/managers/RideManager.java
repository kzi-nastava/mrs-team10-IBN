package com.example.ubercorp.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.RideService;
import com.example.ubercorp.dto.GetRideDTO;
import com.example.ubercorp.dto.GetRideDetailsDTO;
import com.example.ubercorp.dto.RideDTO;

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

    public void loadDriverRides ( int page, int size, Callback<GetRideDTO> callback){
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);

        Call<GetRideDTO> call =
                api.getRidesDriver("Bearer " + token, page, size);

        call.enqueue(callback);
    }

    public void loadRideDetails(Long id, Callback<GetRideDetailsDTO> callback){
        String token = getToken();
        if (token == null) return;

        RideService api = ApiClient.getInstance().createService(RideService.class);

        Call<GetRideDetailsDTO> call = api.getRide("Bearer " + token, id);

        call.enqueue(callback);
    }

}
