package com.example.ubercorp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ubercorp.R;
import com.example.ubercorp.activities.enums.RideStatus;
import com.example.ubercorp.dto.CoordinateDTO;
import com.example.ubercorp.dto.GetRideDetailsDTO;
import com.example.ubercorp.managers.RideManager;
import com.example.ubercorp.managers.RouteManager;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TrackingRouteFragment extends Fragment {
    private MapView mapView;
    private Long rideID;
    private RouteManager routeManager;
    private RideManager rideManager;
    private GetRideDetailsDTO ride;
    private List<GeoPoint> route;
    private List<GeoPoint> stations;
    private TextView infoText;
    private Marker marker;
    private int lastPassedStation;
    private boolean vehicleIsMoving = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tracking_route, container, false);
        SharedPreferences sharedPref = this.getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        if(getArguments() != null) rideID = getArguments().getLong("RideId");
        else rideID = sharedPref.getLong("RideId", -1L);
        Log.d("RideId", Long.toString(rideID));
        mapView = view.findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(12.75);
        mapView.getController().setCenter(new GeoPoint(45.242, 19.8227));

        infoText = view.findViewById(R.id.info_text);

        routeManager = new RouteManager(mapView, this.getContext());
        rideManager = new RideManager(this.getContext());

        marker = new Marker(mapView);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_current_loc));

        Button finishButton = view.findViewById(R.id.finishButton);
        Button panicButton = view.findViewById(R.id.panicButton);

        rideManager.getRide(rideID, new Callback<GetRideDetailsDTO>() {
            @Override
            public void onResponse(Call<GetRideDetailsDTO> call, Response<GetRideDetailsDTO> response) {
                if(response.isSuccessful()){
                    ride = response.body();
                    stations = new ArrayList<>();
                    for (CoordinateDTO point : response.body().getRoute().getStations()){
                        stations.add(point.toGeoPoint());
                    }
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            route = routeManager.getRoute(stations);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    routeManager.drawRoute(route, stations);
                                    marker.setPosition(stations.get(0));
                                    marker.setVisible(true);
                                    mapView.getOverlays().add(marker);
                                    mapView.invalidate();
                                    moveVehicle();
                                }
                            });
                        }
                    });
                    thread.start();
                }else{
                    infoText.setText("No active routes to track!");
                }
            }

            @Override
            public void onFailure(Call<GetRideDetailsDTO> call, Throwable t) {
                infoText.setText("Route fetching failed! Check your Internet connection and try again!");
            }
        });

        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d("Saving", "Saving state");
        SharedPreferences sharedPref = this.getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("RideId", rideID);
        editor.apply();
    }

    private void moveVehicle() {
        new Thread(() -> {
            while(vehicleIsMoving){
                rideManager.getRide(rideID, new Callback<GetRideDetailsDTO>() {
                    @Override
                    public void onResponse(Call<GetRideDetailsDTO> call, Response<GetRideDetailsDTO> response) {
                        if(response.isSuccessful()){
                            if(response.body().getStatus() == RideStatus.Finished){
                                vehicleIsMoving = false;
                                // dodati akciju koja ga vraća na homepage
                            }
                            if(response.body().getStatus() == RideStatus.Panic){
                                vehicleIsMoving =  false;
                                infoText.setText("Panic signal has been broadcast. Help is on the way. Please remain calm.");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<GetRideDetailsDTO> call, Throwable t) {
                        infoText.setText("You have been disconnected from the server! Check your Internet connection!");
                    }
                });
                updateVehicleLocation();
                checkPassedStation();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private void updateVehicleLocation() {

        executor.execute(() -> {
            GeoPoint pos = calculateProgressPosition();
            requireActivity().runOnUiThread(() -> {
                    marker.setPosition(
                            new GeoPoint(
                                    pos.getLatitude(),
                                    pos.getLongitude()
                            )
                    );
                mapView.invalidate();
            });
            Log.d("Updated location", marker.getPosition().toDoubleString());
        });
    }

    private GeoPoint calculateProgressPosition() {
        long now = System.currentTimeMillis();

        LocalDateTime startLdt = LocalDateTime.parse(ride.getStartTime());
        LocalDateTime endLdt = LocalDateTime.parse(ride.getEndTime());

        long start = startLdt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long end = endLdt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        float progress = (float)(now - start) / (end - start);
        progress = Math.max(0f, Math.min(1f, progress));

        String remainingTime = String.format("Estimated time arrival in: %d minutes", (end - now) / 60000);
        infoText.setText(remainingTime);

        int index = (int)(progress * (route.size() - 1));
        return route.get(index);
    }

    private void checkPassedStation(){
        for(int i = lastPassedStation; i < stations.size(); i++){
            if(marker.getPosition().distanceToAsDouble(stations.get(i)) < 50){
                lastPassedStation = i + 1;
            }
        }
    }
}