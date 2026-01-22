package com.example.ubercorp.managers;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.UserService;
import com.example.ubercorp.dto.*;
import com.example.ubercorp.utils.JwtUtils;

import retrofit2.Call;
import retrofit2.Callback;
import java.util.Map;

public class ProfileManager {
    private final Context context;
    private ProfileUpdateListener listener;

    public interface ProfileUpdateListener {
        void onProfileLoaded(CreateUserDTO user, AccountDTO account);
        void onProfileUpdateSuccess();
        void onProfileUpdateFailed(String error);
        void onPasswordChangeSuccess(String message);
        void onPasswordChangeFailed(String error);
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
                    if (JwtUtils.getRoleFromToken(token).equals("driver")) {
                        loadProfile();
                    }
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

    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        String token = getToken();
        if (token == null) {
            listener.onPasswordChangeFailed("No auth token");
            return;
        }

        UserService userApi = ApiClient.getInstance().createService(UserService.class);
        Call<okhttp3.ResponseBody> call = userApi.changePassword("Bearer " + token, changePasswordDTO);

        call.enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                android.util.Log.d("ProfileManager", "Response code: " + response.code());

                if (response.isSuccessful()) {
                    String message = "Password changed successfully";

                    try {
                        if (response.body() != null) {
                            String responseString = response.body().string();
                            android.util.Log.d("ProfileManager", "Response body: " + responseString);

                            if (!responseString.isEmpty()) {
                                try {
                                    org.json.JSONObject jsonObject = new org.json.JSONObject(responseString);
                                    if (jsonObject.has("message")) {
                                        message = jsonObject.getString("message");
                                    }
                                } catch (org.json.JSONException e) {
                                    android.util.Log.e("ProfileManager", "Error parsing JSON: " + e.getMessage());
                                    message = responseString;
                                }
                            }
                        }
                    } catch (Exception e) {
                        android.util.Log.d("ProfileManager", "No body or error reading body: " + e.getMessage());
                    }

                    android.util.Log.d("ProfileManager", "Success: " + message);
                    listener.onPasswordChangeSuccess(message);
                } else {
                    String errorMsg = "Failed to change password";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                            android.util.Log.e("ProfileManager", "Error body: " + errorMsg);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("ProfileManager", "Error reading error body", e);
                    }
                    listener.onPasswordChangeFailed(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                android.util.Log.e("ProfileManager", "Network error: " + t.getMessage(), t);
                listener.onPasswordChangeFailed(t.getMessage());
            }
        });
    }
}