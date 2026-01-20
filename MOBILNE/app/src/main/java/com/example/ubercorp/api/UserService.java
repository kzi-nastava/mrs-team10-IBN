package com.example.ubercorp.api;

import com.example.ubercorp.dto.CreateUserDTO;
import com.example.ubercorp.dto.GetProfileDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface UserService {

    @GET("api/account/me")
    Call<GetProfileDTO> getUser(@Header("Authorization") String authToken);

    @PUT("api/account/me/profile")
    Call<GetProfileDTO> updateProfile(@Header("Authorization") String authToken, @Body CreateUserDTO updatedUser);
}
