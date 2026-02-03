package com.example.ubercorp.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.VehiclePriceService;
import com.example.ubercorp.dto.VehiclePriceDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehiclePricesManager {
    private final Context context;

    public VehiclePricesManager(Context context) {
        this.context = context;
    }

    private String getToken() {
        SharedPreferences sharedPref = context.getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        return sharedPref.getString("auth_token", null);
    }

    public void getVehiclePrices(Callback<List<VehiclePriceDTO>> callback) {
        String token = getToken();
        if (token == null) return;

        VehiclePriceService api = ApiClient.getInstance().createService(VehiclePriceService.class);
        Call<List<VehiclePriceDTO>> call = api.getVehiclePrices ("Bearer " + token);
        call.enqueue(callback);
    }

    public void saveVehiclePrices(List<VehiclePriceDTO> prices){
        String token = getToken();
        if (token == null) return;

        VehiclePriceService api = ApiClient.getInstance().createService(VehiclePriceService.class);

        api.updateVehiclePrices("Bearer " + token, prices).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Prices saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to save prices!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
