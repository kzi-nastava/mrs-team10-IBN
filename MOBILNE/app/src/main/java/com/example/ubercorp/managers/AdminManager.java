package com.example.ubercorp.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.UserService;
import com.example.ubercorp.dto.DriverChangeRequestDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class AdminManager {
    private final Context context;
    private AdminActionsListener listener;

    public interface AdminActionsListener {
        void onChangeRequestsLoaded(List<DriverChangeRequestDTO> requests);
        void onActionSuccess(String message);
        void onActionFailed(String error);
    }

    public AdminManager(Context context, AdminActionsListener listener) {
        this.context = context;
        this.listener = listener;
    }

    private String getToken() {
        SharedPreferences sharedPref = context.getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        return sharedPref.getString("auth_token", null);
    }

    public void loadChangeRequests() {
        UserService adminApi = ApiClient.getInstance().createService(UserService.class);

        String token = getToken();
        if (token == null) {
            listener.onActionFailed("No auth token found");
            return;
        }

        Call<List<DriverChangeRequestDTO>> call = adminApi.getChangeRequests("Bearer " + token);

        call.enqueue(new Callback<List<DriverChangeRequestDTO>>() {
            @Override
            public void onResponse(Call<List<DriverChangeRequestDTO>> call, retrofit2.Response<List<DriverChangeRequestDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onChangeRequestsLoaded(response.body());
                } else {
                    Log.e("ChangeRequests", "HTTP code: " + response.code());
                    try {
                        Log.e("ChangeRequests", "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    listener.onActionFailed("Failed to load requests");
                }
            }

            @Override
            public void onFailure(Call<List<DriverChangeRequestDTO>> call, Throwable t) {
                listener.onActionFailed(t.getMessage());
            }
        });
    }

    public void approveChangeRequest(Long requestId) {
        UserService adminApi = ApiClient.getInstance().createService(UserService.class);

        String token = getToken();
        if (token == null) {
            listener.onActionFailed("No auth token found");
            return;
        }

        Call<ResponseBody> call = adminApi.approveChange("Bearer " + token, requestId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String message = response.body() != null ? response.body().string() : "Request approved";
                        listener.onActionSuccess(message);
                    } catch (Exception e) {
                        listener.onActionSuccess("Request approved");
                    }
                } else {
                    try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        listener.onActionFailed("Failed: " + err);
                    } catch (Exception e) {
                        listener.onActionFailed("Failed with unknown error");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onActionFailed(t.getMessage());
            }
        });
    }


    public void rejectChangeRequest(Long requestId) {
        UserService adminApi = ApiClient.getInstance().createService(UserService.class);

        String token = getToken();
        if (token == null) {
            listener.onActionFailed("No auth token found");
            return;
        }
        Call<ResponseBody> call = adminApi.rejectChange("Bearer " + token, requestId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String message = response.body() != null ? response.body().string() : "Request rejected";
                        listener.onActionSuccess(message);
                    } catch (Exception e) {
                        listener.onActionSuccess("Request rejected");
                    }
                } else {
                    try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        listener.onActionFailed("Failed: " + err);
                    } catch (Exception e) {
                        listener.onActionFailed("Failed with unknown error");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onActionFailed(t.getMessage());
            }
        });
    }
}