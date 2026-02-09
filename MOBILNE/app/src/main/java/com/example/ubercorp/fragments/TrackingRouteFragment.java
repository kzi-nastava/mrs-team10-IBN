package com.example.ubercorp.fragments;

import static android.view.View.GONE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubercorp.R;
import com.example.ubercorp.activities.enums.RideStatus;
import com.example.ubercorp.dto.CoordinateDTO;
import com.example.ubercorp.dto.FinishedRideDTO;
import com.example.ubercorp.dto.GetRideDetailsDTO;
import com.example.ubercorp.dto.RideMomentDTO;
import com.example.ubercorp.dto.StopRideDTO;
import com.example.ubercorp.managers.RideManager;
import com.example.ubercorp.managers.RouteManager;
import com.example.ubercorp.utils.JwtUtils;

import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TrackingRouteFragment extends Fragment {
    private MapView mapView;
    private String rideToken;
    private RouteManager routeManager;
    private RideManager rideManager;
    private GetRideDetailsDTO ride;
    private List<GeoPoint> route;
    private List<GeoPoint> stations;
    private TextView infoText;
    private Marker marker;
    private double distance;
    private double passedDistance;
    private int lastPassedStation;
    private boolean vehicleIsMoving = true;
    private boolean connected = true;
    private String role;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tracking_route, container, false);
        SharedPreferences sharedPref = this.getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);
        role = JwtUtils.getRoleFromToken(token);
        if(getArguments() != null) rideToken = getArguments().getString("RideToken");
        else rideToken = sharedPref.getString("RideToken", "");
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
        Button complaint = view.findViewById(R.id.reportHere);

        if (role.equals("passenger")){
            finishButton.setVisibility(GONE);
        }else{
            complaint.setVisibility(GONE);
        }

        complaint.setOnClickListener((v) -> {
            Bundle bundle = new Bundle();
            bundle.putLong("RideID", ride.getId());
            Navigation.findNavController(requireView()).navigate(R.id.complaintForm, bundle);
        });

        finishButton.setOnClickListener((v) -> {
            vehicleIsMoving = false;
            if(lastPassedStation == stations.size()){
                RideMomentDTO finished = new RideMomentDTO(LocalDateTime.now().toString());
                rideManager.finishRide(ride.getId(), finished, new Callback<FinishedRideDTO>() {
                    @Override
                    public void onResponse(Call<FinishedRideDTO> call, Response<FinishedRideDTO> response) {
                        // notifikacije o završetku vožnje
                        // promeniti kada dodamo home screen za vozače
                        Navigation.findNavController(requireView()).navigate(
                                R.id.action_incomingRideFragment_to_trackingRouteFragment
                        );
                    }

                    @Override
                    public void onFailure(Call<FinishedRideDTO> call, Throwable t) {
                        Toast toast = Toast.makeText(TrackingRouteFragment.this.getContext(), "Can't finish ride! Check your Internet connection and try again!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            } else {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        rideManager.stopRide(getStopRideDTO(), new Callback<FinishedRideDTO>() {
                            @Override
                            public void onResponse(Call<FinishedRideDTO> call, Response<FinishedRideDTO> response) {
                                // notifikacije o završetku vožnje
                                // promeniti kada dodamo home screen za vozače
                                Navigation.findNavController(requireView()).navigate(
                                        R.id.action_trackingRouteFragment_to_routeFragment
                                );
                            }

                            @Override
                            public void onFailure(Call<FinishedRideDTO> call, Throwable t) {
                                Toast toast = Toast.makeText(TrackingRouteFragment.this.getContext(), "Can't finish ride! Check your Internet connection and try again!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                });
                thread.start();
            }
        });

        panicButton.setOnClickListener((v) -> {
            Toast toast = Toast.makeText(TrackingRouteFragment.this.getContext(), "Press and hold to send a panic signal", Toast.LENGTH_SHORT);
            toast.show();
        });

        panicButton.setOnLongClickListener((v) -> {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    rideManager.panic(getStopRideDTO(), new Callback<FinishedRideDTO>() {
                        @Override
                        public void onResponse(Call<FinishedRideDTO> call, Response<FinishedRideDTO> response) {
                            vehicleIsMoving = false;
                            infoText.setText("Panic signal has been broadcast. Help is on the way. Please remain calm.");
                        }

                        @Override
                        public void onFailure(Call<FinishedRideDTO> call, Throwable t) {
                            Toast toast = Toast.makeText(TrackingRouteFragment.this.getContext(), "Check your Internet connection and try again!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            });
            thread.start();
            return false;
        });

        if (role == "driver") {
            rideManager.getRideByToken(rideToken, new Callback<GetRideDetailsDTO>() {
                @Override
                public void onResponse(Call<GetRideDetailsDTO> call, Response<GetRideDetailsDTO> response) {
                    if (response.isSuccessful()) {
                        ride = response.body();
                        stations = new ArrayList<>();
                        for (CoordinateDTO point : response.body().getRoute().getStations()) {
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
                                        distance = routeManager.getEstimatedDistance();
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
                    } else {
                        infoText.setText("No active routes to track!");
                    }
                }

                @Override
                public void onFailure(Call<GetRideDetailsDTO> call, Throwable t) {
                    infoText.setText("Route fetching failed! Check your Internet connection and try again!");
                }
            });
        }else{
            rideManager.getTrackingRidePassenger(new Callback<GetRideDetailsDTO>() {
                @Override
                public void onResponse(Call<GetRideDetailsDTO> call, Response<GetRideDetailsDTO> response) {
                    if (response.isSuccessful() && response.body().getRoute() != null) {
                        ride = response.body();
                        stations = new ArrayList<>();
                        for (CoordinateDTO point : response.body().getRoute().getStations()) {
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
                                        distance = routeManager.getEstimatedDistance();
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
                    } else {
                        infoText.setText("No active routes to track!");
                    }
                }

                @Override
                public void onFailure(Call<GetRideDetailsDTO> call, Throwable t) {
                    infoText.setText("Route fetching failed! Check your Internet connection and try again!");
                }
            });
        }

        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences sharedPref = this.getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("RideToken", rideToken);
        editor.apply();
    }

    private void moveVehicle() {
        new Thread(() -> {
            while(vehicleIsMoving){
                rideManager.getRideByToken(rideToken, new Callback<GetRideDetailsDTO>() {
                    @Override
                    public void onResponse(Call<GetRideDetailsDTO> call, Response<GetRideDetailsDTO> response) {
                        if(response.isSuccessful()){
                            if(response.body().getStatus() == RideStatus.Finished){
                                vehicleIsMoving = false;
                                Navigation.findNavController(requireView()).navigate(
                                        R.id.action_trackingRouteFragment_to_routeFragment
                                );
                            }
                            if(response.body().getStatus() == RideStatus.Panic){
                                vehicleIsMoving =  false;
                                infoText.setText("Panic signal has been broadcast. Help is on the way. Please remain calm.");
                            }
                        }
                        connected = true;
                    }

                    @Override
                    public void onFailure(Call<GetRideDetailsDTO> call, Throwable t) {
                        infoText.setText("You have been disconnected from the server! Check your Internet connection!");
                        connected = false;
                    }
                });
                if(connected){
                    updateVehicleLocation();
                    checkPassedStation();
                }
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
            if(isAdded()) requireActivity().runOnUiThread(() -> {
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
        LocalDateTime endLdt = LocalDateTime.parse(ride.getEstimatedTimeArrival());

        long start = startLdt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long end = endLdt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        float progress = (float)(now - start) / (end - start);
        progress = Math.max(0f, Math.min(1f, progress));

        String remainingTime = String.format("Estimated time arrival in: %d minutes", (end - now) / 60000);
        requireActivity().runOnUiThread(() -> infoText.setText(remainingTime));

        int index = (int)(progress * (route.size() - 1));
        passedDistance = distance * ((double) index / route.size());
        return route.get(index);
    }

    private void checkPassedStation(){
        for(int i = lastPassedStation; i < stations.size(); i++){
            if(marker.getPosition().distanceToAsDouble(stations.get(i)) < 50){
                Log.d("station passed!", Integer.toString(lastPassedStation));
                lastPassedStation = i + 1;
            }
        }
    }

    private String getCurrentAddress(){
        HttpURLConnection conn = null;
        try {
            URL url = new URL(String.format(Locale.US,
                    "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f",
                    marker.getPosition().getLatitude(), marker.getPosition().getLongitude()));

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "UberCorp/1.0");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(result.toString());

                if (jsonResponse.has("address")) {
                    JSONObject address = jsonResponse.getJSONObject("address");
                    String road = address.optString("road", "Unknown");
                    String houseNumber = address.optString("house_number", "");
                    String addressString = road + " " + houseNumber;

                    return addressString.trim();
                }
            }
        } catch (Exception ex) {
            String log = Log.getStackTraceString(ex);
            Log.e("Exception", log);
            Toast.makeText(getContext(),
                    "Can't get address! Check your Internet connection and try again!",
                    Toast.LENGTH_SHORT).show();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    private StopRideDTO getStopRideDTO(){
        StopRideDTO stopped = new StopRideDTO();
        stopped.setId(ride.getId());
        stopped.setPassed(lastPassedStation);
        stopped.setLat(marker.getPosition().getLatitude());
        stopped.setLon(marker.getPosition().getLongitude());
        stopped.setFinishTime(LocalDateTime.now().toString());
        stopped.setDistance(passedDistance);
        stopped.setAddress(getCurrentAddress());

        return stopped;
    }
}