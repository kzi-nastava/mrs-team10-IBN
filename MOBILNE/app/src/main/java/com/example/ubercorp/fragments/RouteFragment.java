package com.example.ubercorp.fragments;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubercorp.R;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.RideService;
import com.example.ubercorp.dto.CoordinateDTO;
import com.example.ubercorp.dto.RideDTO;
import com.example.ubercorp.managers.RideManager;
import com.example.ubercorp.managers.RouteManager;
import com.example.ubercorp.utils.JwtUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteFragment extends Fragment {

    private static final String TAG = "RouteFragment";
    private static final int UPDATE_INTERVAL = 3000; // ms

    // UI Components
    private MapView mapView;
    private EditText startAddressInput, endAddressInput;
    private Button drawRouteButton;
    private Button confirmButton;
    private Button addStopBtn;
    private Button removeStopBtn;
    private Button orderRideButton;
    private CardView locationDisplay;
    private CardView dropdownContent;
    private LinearLayout stopsContainer;
    private TextView locationText;
    private TextView timeText;

    // Data
    private List<EditText> stationInputList = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private FolderOverlay vehicleLayer;
    private Map<Long, Marker> vehicleMarkers = new HashMap<>();

    // Managers
    private RideManager rideManager;
    private RouteManager routeManager;

    // State
    private boolean isDropdownOpen = false;
    private double estimatedDistance = 0.0;
    private int estimatedDuration = 0;

    // Threading
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ExecutorService executor;

    private Call<List<RideDTO>> activeRidesCall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        executor = Executors.newSingleThreadExecutor();

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        rideManager = new RideManager(requireContext());

        initializeMap(view);
        initializeViews(view);
        setupListeners();
        setVehiclesOnMapPeriodic();

        return view;
    }

    private void initializeMap(View view) {
        mapView = view.findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(12.75);
        mapView.getController().setCenter(new GeoPoint(45.242, 19.8227));

        vehicleLayer = new FolderOverlay();
        mapView.getOverlays().add(vehicleLayer);
        mapView.invalidate();

        routeManager = new RouteManager(mapView, requireContext());
    }

    private void initializeViews(View view) {
        startAddressInput = view.findViewById(R.id.startAddress);
        endAddressInput = view.findViewById(R.id.endAddress);
        drawRouteButton = view.findViewById(R.id.drawRoute);
        locationDisplay = view.findViewById(R.id.locationDisplay);
        dropdownContent = view.findViewById(R.id.dropdownContent);
        confirmButton = view.findViewById(R.id.confirmBtn);
        addStopBtn = view.findViewById(R.id.addStopBtn);
        removeStopBtn = view.findViewById(R.id.removeStopBtn);
        stopsContainer = view.findViewById(R.id.stopsContainer);
        locationText = view.findViewById(R.id.locationText);
        timeText = view.findViewById(R.id.timeText);
        orderRideButton = view.findViewById(R.id.orderRide);

        if (!isUserLoggedIn() || !isUserPassenger()) {
            orderRideButton.setVisibility(GONE);
        } else {
            orderRideButton.setVisibility(VISIBLE);
        }
    }

    private boolean isUserLoggedIn() {
        Context context = getContext();
        if (context == null) return false;

        SharedPreferences sharedPref =
                context.getSharedPreferences("uber_corp", Context.MODE_PRIVATE);

        String token = sharedPref.getString("auth_token", null);
        return token != null && !token.isEmpty();
    }


    private boolean isUserPassenger() {
        Context context = getContext();
        if (context == null) return false;

        SharedPreferences sharedPref =
                context.getSharedPreferences("uber_corp", Context.MODE_PRIVATE);

        String token = sharedPref.getString("auth_token", null);
        if (token == null || token.isEmpty()) return false;

        String role = JwtUtils.getRoleFromToken(token);
        if (role == null) return false;

        return role.equalsIgnoreCase("passenger");
    }


    private void setupListeners() {
        drawRouteButton.setOnClickListener(v -> handleDrawRoute());
        locationDisplay.setOnClickListener(v -> toggleDropdown());
        confirmButton.setOnClickListener(v -> {
            toggleDropdown();
            displayRouteAsText();
        });
        addStopBtn.setOnClickListener(v -> addStop());
        removeStopBtn.setOnClickListener(v -> removeStop());
        orderRideButton.setOnClickListener(v -> checkOngoingRideAndNavigate());
    }

    private void handleDrawRoute() {
        String startAddress = startAddressInput.getText().toString().trim();
        String endAddress = endAddressInput.getText().toString().trim();

        if (startAddress.isEmpty() || endAddress.isEmpty()) {
            showToast("Please enter both addresses");
            return;
        }

        fetchRoute(startAddress, endAddress);
    }

    private String getToken() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        return sharedPref.getString("auth_token", null);
    }

    private void checkOngoingRideAndNavigate() {
        RideService api = ApiClient.getInstance().createService(RideService.class);
        api.getOngoingRide("Bearer " + getToken()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean hasOngoingRide = response.body();

                    if (hasOngoingRide) {
                        Toast.makeText(requireContext(),
                                "You already have an ongoing ride!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        navigateToOrderRide();
                    }
                } else {
                    Toast.makeText(requireContext(),
                            "Failed to check ongoing ride",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void navigateToOrderRide() {
        String startAddress = startAddressInput.getText().toString().trim();
        String endAddress = endAddressInput.getText().toString().trim();

        Bundle args = new Bundle();
        args.putString("fromLocation", startAddress);
        args.putString("toLocation", endAddress);

        ArrayList<String> stopsList = new ArrayList<>();
        for (EditText stationInput : stationInputList) {
            String stop = stationInput.getText().toString().trim();
            if (!stop.isEmpty()) {
                stopsList.add(stop);
            }
        }
        args.putStringArrayList("stops", stopsList);
        args.putDouble("estimatedDistance", estimatedDistance);
        args.putInt("estimatedDuration", estimatedDuration);

        Navigation.findNavController(requireView())
                .navigate(R.id.action_routeFragment_to_orderRideFragment, args);
    }

    private void addStop() {
        EditText stationInput = new EditText(getContext());
        stationInput.setHint("Enter stop location");
        stationInput.setBackgroundResource(R.drawable.input_background);

        int paddingPx = (int) (10 * getResources().getDisplayMetrics().density);
        int marginBottomPx = (int) (12 * getResources().getDisplayMetrics().density);

        stationInput.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        stationInput.setTextSize(14);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, marginBottomPx);
        stationInput.setLayoutParams(params);

        stationInputList.add(stationInput);
        stopsContainer.addView(stationInput);
    }

    private void removeStop() {
        if (stationInputList.isEmpty()) {
            return;
        }
        EditText removed = stationInputList.remove(stationInputList.size() - 1);
        stopsContainer.removeView(removed);
    }

    private void toggleDropdown() {
        isDropdownOpen = !isDropdownOpen;
        if (isDropdownOpen) {
            locationDisplay.setVisibility(GONE);
            dropdownContent.setVisibility(VISIBLE);
        } else {
            locationDisplay.setVisibility(VISIBLE);
            dropdownContent.setVisibility(GONE);
        }
    }

    private void displayRouteAsText() {
        String startAddress = startAddressInput.getText().toString().trim();
        String endAddress = endAddressInput.getText().toString().trim();

        if (startAddress.isEmpty() || endAddress.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder(startAddress);
        for (EditText stationInput : stationInputList) {
            String stationText = stationInput.getText().toString().trim();
            if (!stationText.isEmpty()) {
                sb.append(" → ").append(stationText);
            }
        }
        sb.append(" → ").append(endAddress);
        locationText.setText(sb.toString());
    }

    private void fetchRoute(String startAddress, String endAddress) {
        if (executor == null || executor.isShutdown()) {
            Log.w(TAG, "Executor is not available");
            return;
        }

        executor.execute(() -> {
            try {
                GeoPoint startPoint = getCoordinatesFromAddress(startAddress);
                GeoPoint endPoint = getCoordinatesFromAddress(endAddress);

                if (startPoint == null || endPoint == null) {
                    showToast("Invalid address provided");
                    return;
                }

                List<GeoPoint> stations = new ArrayList<>();
                stations.add(startPoint);

                for (EditText stationInput : stationInputList) {
                    String stationText = stationInput.getText().toString().trim();
                    if (!stationText.isEmpty()) {
                        GeoPoint station = getCoordinatesFromAddress(stationText);
                        if (station != null) {
                            stations.add(station);
                        }
                    }
                }

                stations.add(endPoint);

                List<GeoPoint> routePoints = getRoute(stations);

                if (routePoints == null || routePoints.isEmpty()) {
                    showToast("Unable to fetch route");
                    return;
                }

                Activity activity = getActivity();
                if (activity == null || !isAdded()) return;

                activity.runOnUiThread(() -> {
                    if (!isAdded()) return;
                    drawRoute(routePoints, stations);
                });

            } catch (Exception e) {
                Log.e(TAG, "Error fetching route", e);
                showToast("Error fetching route: " + e.getMessage());
            }
        });
    }

    private GeoPoint getCoordinatesFromAddress(String address) {
        try {
            Thread.sleep(1000);

            String encodedAddress = URLEncoder.encode(address + ", Novi Sad, Serbia", "UTF-8");
            String urlString = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedAddress;

            Log.d(TAG, "Geocoding: " + address);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "UberCorp/1.0");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                JSONArray results = new JSONArray(result.toString());
                if (results.length() > 0) {
                    JSONObject resultObj = results.getJSONObject(0);
                    double lat = resultObj.getDouble("lat");
                    double lon = resultObj.getDouble("lon");

                    Log.d(TAG, "Found coordinates: " + lat + ", " + lon);
                    return new GeoPoint(lat, lon);
                }
            }

            conn.disconnect();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Geocoding interrupted", e);
        } catch (Exception e) {
            Log.e(TAG, "Geocoding error", e);
        }

        return null;
    }

    private String buildQueryString(List<GeoPoint> stations) {
        StringBuilder sb = new StringBuilder("https://router.project-osrm.org/route/v1/driving/");

        for (int i = 0; i < stations.size(); i++) {
            GeoPoint station = stations.get(i);
            sb.append(station.getLongitude()).append(",").append(station.getLatitude());
            if (i < stations.size() - 1) {
                sb.append(";");
            }
        }

        sb.append("?overview=full&geometries=geojson");
        return sb.toString();
    }

    private List<GeoPoint> getRoute(List<GeoPoint> stations) {
        try {
            String urlString = buildQueryString(stations);
            Log.d(TAG, "Fetching route from OSRM");

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(result.toString());
                JSONObject route = json.getJSONArray("routes").getJSONObject(0);

                JSONArray coordinates = route.getJSONObject("geometry").getJSONArray("coordinates");
                double durationSeconds = route.getDouble("duration");
                double distanceMeters = route.getDouble("distance");

                estimatedDuration = (int) (durationSeconds / 60);
                estimatedDistance = distanceMeters / 1000.0;

                Activity activity = getActivity();
                if (activity != null && isAdded()) {
                    activity.runOnUiThread(() -> {
                        if (isAdded()) {
                            timeText.setText(String.format("Estimated time: %d minutes (%.2f km)",
                                    estimatedDuration, estimatedDistance));
                        }
                    });
                }

                List<GeoPoint> routePoints = new ArrayList<>();
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = coordinates.getJSONArray(i);
                    double lon = point.getDouble(0);
                    double lat = point.getDouble(1);
                    routePoints.add(new GeoPoint(lat, lon));
                }

                conn.disconnect();
                Log.d(TAG, "Route fetched successfully: " + routePoints.size() + " points");
                return routePoints;
            }

            conn.disconnect();

        } catch (IOException | JSONException e) {
            Log.e(TAG, "Route fetch error", e);
        }

        return null;
    }

    private void drawRoute(List<GeoPoint> routePoints, List<GeoPoint> stations) {
        mapView.getOverlays().clear();
        mapView.getOverlays().add(vehicleLayer);

        Polyline routeLine = new Polyline();
        routeLine.setPoints(routePoints);
        routeLine.setColor(0xFF0000FF);
        routeLine.setWidth(10.0f);
        mapView.getOverlays().add(routeLine);

        markStations(stations);

        if (!routePoints.isEmpty()) {
            IMapController mapController = mapView.getController();
            mapController.setZoom(14.0);
            mapController.setCenter(routePoints.get(routePoints.size() / 2));
        }

        mapView.invalidate();
        Log.d(TAG, "Route drawn on map");
    }

    private void markStations(List<GeoPoint> stations) {
        for (Marker marker : markers) {
            mapView.getOverlays().remove(marker);
        }
        markers.clear();

        for (int i = 0; i < stations.size(); i++) {
            GeoPoint station = stations.get(i);
            Marker marker = new Marker(mapView);
            marker.setPosition(station);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            if (i == 0) {
                marker.setTitle("Start");
            } else if (i == stations.size() - 1) {
                marker.setTitle("End");
            } else {
                marker.setTitle("Stop " + i);
            }

            marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_location));
            markers.add(marker);
            mapView.getOverlays().add(marker);
        }
    }

    private void setVehiclesOnMapPeriodic() {
        handler.post(updateVehiclesRunnable);
    }

    private final Runnable updateVehiclesRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAdded()) {
                Log.d(TAG, "Fragment not added, skipping vehicle update");
                return;
            }

            if (activeRidesCall != null && !activeRidesCall.isCanceled()) {
                activeRidesCall.cancel();
            }

            activeRidesCall = rideManager.getActiveRides(new Callback<List<RideDTO>>() {
                @Override
                public void onResponse(Call<List<RideDTO>> call, Response<List<RideDTO>> response) {
                    if (!isAdded()) return;

                    if (response.isSuccessful() && response.body() != null) {
                        updateVehiclesLocation(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch rides: " + response.code());
                    }

                    if (isAdded()) {
                        handler.postDelayed(updateVehiclesRunnable, UPDATE_INTERVAL);
                    }
                }

                @Override
                public void onFailure(Call<List<RideDTO>> call, Throwable t) {
                    if (!isAdded()) return;

                    if (!call.isCanceled()) {
                        Log.e(TAG, "Failed to load vehicles", t);
                    }

                    if (isAdded()) {
                        handler.postDelayed(updateVehiclesRunnable, UPDATE_INTERVAL);
                    }
                }
            });
        }
    };

    private void updateVehiclesLocation(List<RideDTO> rides) {
        if (executor == null || executor.isShutdown()) {
            Log.w(TAG, "Executor not available, skipping vehicle update");
            return;
        }

        if (!isAdded()) {
            Log.w(TAG, "Fragment not added, skipping vehicle update");
            return;
        }

        executor.execute(() -> {
            Map<Long, GeoPoint> calculatedPositions = new HashMap<>();

            for (RideDTO ride : rides) {
                if (ride.isBusy()) {
                    GeoPoint pos = calculateProgressPosition(ride);
                    if (pos != null) {
                        calculatedPositions.put(ride.getId(), pos);
                    }
                }
            }

            Activity activity = getActivity();
            if (activity == null || !isAdded()) return;

            activity.runOnUiThread(() -> {
                if (!isAdded()) return;

                for (RideDTO ride : rides) {
                    Marker marker = vehicleMarkers.get(ride.getId());

                    if (marker == null) {
                        marker = new Marker(mapView);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                        vehicleMarkers.put(ride.getId(), marker);
                        vehicleLayer.add(marker);
                    }

                    if (ride.isBusy() && calculatedPositions.containsKey(ride.getId())) {
                        marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.red_car));
                        marker.setPosition(calculatedPositions.get(ride.getId()));
                    } else {
                        marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.green_car));
                        if (ride.getVehicleLocation() != null) {
                            marker.setPosition(new GeoPoint(
                                    ride.getVehicleLocation().getLatitude(),
                                    ride.getVehicleLocation().getLongitude()
                            ));
                        }
                    }
                }

                mapView.invalidate();
            });
        });
    }

    private GeoPoint calculateProgressPosition(RideDTO ride) {
        try {
            long now = System.currentTimeMillis();

            LocalDateTime startLdt = LocalDateTime.parse(ride.getStartTime());
            LocalDateTime endLdt = LocalDateTime.parse(ride.getEstimatedTimeArrival());

            long start = startLdt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long end = endLdt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            float progress = (float) (now - start) / (end - start);
            progress = Math.max(0f, Math.min(1f, progress));

            List<GeoPoint> points = new ArrayList<>();
            for (CoordinateDTO c : ride.getRoute().getStations()) {
                points.add(new GeoPoint(c.getLat(), c.getLon()));
            }

            if (routeManager == null) return null;

            List<GeoPoint> routePoints = routeManager.getRoute(points);
            if (routePoints == null || routePoints.isEmpty()) return null;

            int index = (int) (progress * (routePoints.size() - 1));
            return routePoints.get(index);

        } catch (Exception e) {
            Log.e(TAG, "Error calculating vehicle position", e);
            return null;
        }
    }

    private void showToast(String message) {
        Activity activity = getActivity();
        if (activity == null || !isAdded()) return;

        activity.runOnUiThread(() -> {
            if (!isAdded() || getContext() == null) return;
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView - cleaning up");

        handler.removeCallbacks(updateVehiclesRunnable);

        if (activeRidesCall != null && !activeRidesCall.isCanceled()) {
            activeRidesCall.cancel();
            Log.d(TAG, "Cancelled active rides call");
        }

        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    Log.w(TAG, "Executor forcefully shut down");
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            Log.d(TAG, "Executor shut down");
        }

        if (mapView != null) {
            mapView.onDetach();
        }
    }
}