package com.example.ubercorp.api;

import com.example.ubercorp.dto.ChangePasswordDTO;
import com.example.ubercorp.dto.CreateUserDTO;
import com.example.ubercorp.dto.DriverChangeRequestDTO;
import com.example.ubercorp.dto.GetProfileDTO;
import com.example.ubercorp.dto.PageDTO;
import com.example.ubercorp.dto.UserDTO;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @GET("api/account/me")
    Call<GetProfileDTO> getUser(@Header("Authorization") String authToken);

    @PUT("api/account/me/profile")
    Call<GetProfileDTO> updateProfile(@Header("Authorization") String authToken, @Body CreateUserDTO updatedUser);

    @GET("api/account/change-requests")
    Call<List<DriverChangeRequestDTO>> getChangeRequests(@Header("Authorization") String authToken);

    @POST("api/account/approve-change/{id}")
    Call<ResponseBody> approveChange(@Header("Authorization") String authToken, @Path("id") Long id);

    @POST("api/account/reject-change/{id}")
    Call<ResponseBody> rejectChange(@Header("Authorization") String authToken, @Path("id") Long id);

    @PUT("api/account/me/change-password")
    Call<ResponseBody> changePassword(
            @Header("Authorization") String token,
            @Body ChangePasswordDTO changePasswordDTO
    );

    @GET("api/account/drivers")
    Call<PageDTO<UserDTO>> getDrivers(
            @Header("Authorization") String authToken,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/account/passengers")
    Call<PageDTO<UserDTO>> getPassengers(
            @Header("Authorization") String authToken,
            @Query("page") int page,
            @Query("size") int size
    );

    @PUT("api/account/block")
    Call<String> blockUser(
            @Header("Authorization") String authToken,
            @Body Map<String, String> body
    );

    @PUT("api/account/unblock")
    Call<String> unblockUser(
            @Header("Authorization") String authToken,
            @Body Map<String, String> body
    );
}
