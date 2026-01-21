package com.example.ubercorp.managers;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.UserService;
import com.example.ubercorp.dto.*;
import retrofit2.Call;
import retrofit2.Callback;

public class ProfileManager {
    private final Context context;
    private ProfileUpdateListener listener;

    public interface ProfileUpdateListener {
        void onProfileLoaded(CreateUserDTO user, AccountDTO account);
        void onProfileUpdateSuccess();
        void onProfileUpdateFailed(String error);
    }

    public ProfileManager(Context context, ProfileUpdateListener listener) {
        this.context = context;
        this.listener = listener;
    }

    private String getToken() {
        SharedPreferences sharedPref = context.getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        return sharedPref.getString("auth_token", null);
    }

    public void loadProfile() {
        String token = getToken();
        if (token == null) {
            listener.onProfileUpdateFailed("No auth token found");
            return;
        }

        UserService userApi = ApiClient.getInstance().createService(UserService.class);
        Call<GetProfileDTO> call = userApi.getUser("Bearer " + token);

        call.enqueue(new Callback<GetProfileDTO>() {
            @Override
            public void onResponse(Call<GetProfileDTO> call, retrofit2.Response<GetProfileDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetProfileDTO profile = response.body();
                    listener.onProfileLoaded(
                            profile.getCreatedUserDTO(),
                            profile.getAccountDTO()
                    );
                } else {
                    listener.onProfileUpdateFailed("Failed to load profile");
                }
            }

            @Override
            public void onFailure(Call<GetProfileDTO> call, Throwable t) {
                listener.onProfileUpdateFailed(t.getMessage());
            }
        });
    }

    public void updateProfile(CreateUserDTO updateDTO) {
        String token = getToken();
        if (token == null) {
            listener.onProfileUpdateFailed("No auth token");
            return;
        }

        UserService userApi = ApiClient.getInstance().createService(UserService.class);
        Call<GetProfileDTO> call = userApi.updateProfile("Bearer " + token, updateDTO);

        call.enqueue(new Callback<GetProfileDTO>() {
            @Override
            public void onResponse(Call<GetProfileDTO> call, retrofit2.Response<GetProfileDTO> response) {
                if (response.isSuccessful()) {
                    listener.onProfileUpdateSuccess();
                } else {
                    listener.onProfileUpdateFailed("Update failed");
                }
            }

            @Override
            public void onFailure(Call<GetProfileDTO> call, Throwable t) {
                listener.onProfileUpdateFailed(t.getMessage());
            }
        });
    }
}