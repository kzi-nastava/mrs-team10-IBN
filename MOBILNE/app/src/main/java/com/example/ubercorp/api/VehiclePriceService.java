package com.example.ubercorp.api;
import com.example.ubercorp.dto.VehiclePriceDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface VehiclePriceService {
    @GET("api/prices")
    Call<List<VehiclePriceDTO>> getVehiclePrices(@Header("Authorization") String authToken);

    @PUT("api/prices")
    Call<Void> updateVehiclePrices(@Header("Authorization") String authToken,
                                                    @Body List<VehiclePriceDTO> updatedPrices);
}
