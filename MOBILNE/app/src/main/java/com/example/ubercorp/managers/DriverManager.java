package com.example.ubercorp.managers;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.DriverService;
import com.example.ubercorp.dto.*;
import retrofit2.Call;
import retrofit2.Callback;

public class DriverManager {
    private final Context context;
    private DriverActionsListener listener;

    public interface DriverActionsListener {
        void onDriverDataLoaded(DriverDTO driver);
        void onVehicleUpdateSuccess();
        void onActionFailed(String error);
    }

    public DriverManager(Context context, DriverActionsListener listener) {
        this.context = context;
        this.listener = listener;
    }

    private String getToken() {
        SharedPreferences sharedPref = context.getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        return sharedPref.getString("auth_token", null);
    }

    public void loadDriverData() {
        String token = getToken();
        if (token == null) {
            listener.onActionFailed("No auth token");
            return;
        }

        DriverService driverApi = ApiClient.getInstance().createService(DriverService.class);
        Call<DriverDTO> call = driverApi.getDriverProfile("Bearer " + token);

        call.enqueue(new Callback<DriverDTO>() {
            @Override
            public void onResponse(Call<DriverDTO> call, retrofit2.Response<DriverDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onDriverDataLoaded(response.body());
                } else {
                    listener.onActionFailed("Failed to load driver data");
                }
            }

            @Override
            public void onFailure(Call<DriverDTO> call, Throwable t) {
                listener.onActionFailed(t.getMessage());
            }
        });
    }

    public void submitChangeRequest(UpdateDriverDTO updateDTO) {
        String token = getToken();
        if (token == null) {
            listener.onActionFailed("No auth token");
            return;
        }

        DriverService driverService = ApiClient.getInstance().createService(DriverService.class);
        Call<Void> call = driverService.submitDriverChangeRequest("Bearer " + token, updateDTO);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onVehicleUpdateSuccess();
                } else {
                    listener.onActionFailed("Failed to submit change request: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onActionFailed(t.getMessage());
            }
        });
    }
}