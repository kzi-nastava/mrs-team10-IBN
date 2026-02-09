package com.example.ubercorp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubercorp.R;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.DriverService;
import com.example.ubercorp.dto.CoordinateDTO;
import com.example.ubercorp.dto.GetStatusDTO;
import com.example.ubercorp.managers.DriverManager;
import com.example.ubercorp.utils.JwtUtils;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHomeFragment extends Fragment {
    private EditText locationInput;
    private Button updateLocation;
    private MapView mapView;
    private View indicator;
    private TextView statusText;
    private Button toggleOnlineButton;
    private Marker marker;
    private boolean isOnline;
    private boolean isBlocked;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_home, container, false);

        locationInput = view.findViewById(R.id.locationInput);
        updateLocation = view.findViewById(R.id.updateLocation);
        mapView = view.findViewById(R.id.map);
        indicator = view.findViewById(R.id.indicator);
        statusText = view.findViewById(R.id.statusText);
        toggleOnlineButton = view.findViewById(R.id.toggleOnlineButton);

        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(12.75);
        mapView.getController().setCenter(new GeoPoint(45.242, 19.8227));

        marker = new Marker(mapView);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_current_loc));

        SharedPreferences sharedPref = this.getContext().getSharedPreferences("uber_corp", MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);
        Call<GetStatusDTO> getStatus = ApiClient.getInstance().createService(DriverService.class).getStatus("Bearer " + token);
        getStatus.enqueue(new Callback<GetStatusDTO>() {
            @Override
            public void onResponse(Call<GetStatusDTO> call, Response<GetStatusDTO> response) {
                isOnline = response.body().isActive();
                isBlocked = response.body().isBlocked();
                updateView();
            }

            @Override
            public void onFailure(Call<GetStatusDTO> call, Throwable t) {
                isOnline = false;
                isBlocked = false;
                updateView();
                Toast toast = Toast.makeText(DriverHomeFragment.this.getContext(), "Check your Internet connection and try again!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        Log.d("Status", Boolean.toString(isOnline));
        updateView();

        updateLocation.setOnClickListener((v) -> {
            Call<CoordinateDTO> call = ApiClient.getInstance().createService(DriverService.class).updateLocation("Bearer " + token, locationInput.getText().toString().trim());
            call.enqueue(new Callback<CoordinateDTO>() {
                @Override
                public void onResponse(Call<CoordinateDTO> call, Response<CoordinateDTO> response) {
                    if(response.isSuccessful()){
                        marker.setPosition(new GeoPoint(response.body().getLat(), response.body().getLon()));
                        marker.setVisible(true);
                        mapView.getOverlays().add(marker);
                        mapView.invalidate();
                    } else {
                        Toast toast = Toast.makeText(DriverHomeFragment.this.getContext(), "Improper address!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                @Override
                public void onFailure(Call<CoordinateDTO> call, Throwable t) {
                    Toast toast = Toast.makeText(DriverHomeFragment.this.getContext(), "Check your Internet connection and try again!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        });
        toggleOnlineButton.setOnClickListener((v) -> {
            if (isBlocked) {
                Toast.makeText(getContext(), "Your account is blocked", Toast.LENGTH_SHORT).show();
                return;
            }
            Call<Void> call = ApiClient.getInstance().createService(DriverService.class).toggleDriverStatus("Bearer " + token, !isOnline);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()){
                        isOnline = !isOnline;
                        updateView();
                    } else {
                        try {
                            Log.d("Error", response.errorBody().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Toast toast = Toast.makeText(DriverHomeFragment.this.getContext(), "Something's wrong!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast toast = Toast.makeText(DriverHomeFragment.this.getContext(), "Check your Internet connection and try again!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        });

        return view;
    }

    //@Override
    //public void onPause(){
    //    super.onPause();
    //    SharedPreferences sharedPref = this.getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
    //    SharedPreferences.Editor editor = sharedPref.edit();
    //    editor.putBoolean("Status", isOnline);
    //    editor.apply();
    //}

    private void updateView(){
        if(isOnline && !isBlocked){
            indicator.setBackground(getResources().getDrawable(R.drawable.ic_circle));
            indicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bright_green)));
            statusText.setText("ONLINE");
            toggleOnlineButton.setBackgroundColor(getResources().getColor(R.color.bright_green));
            toggleOnlineButton.setText("GO OFFLINE");
            updateLocation.setEnabled(true);
        } else {
            indicator.setBackground(getResources().getDrawable(R.drawable.ic_circle));
            indicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            statusText.setText("OFFLINE");
            toggleOnlineButton.setBackgroundColor(getResources().getColor(R.color.red));
            toggleOnlineButton.setText("GO ONLINE");
            updateLocation.setEnabled(false);
        }
    }
}