package com.example.ubercorp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.util.TypedValueCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubercorp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RouteFragment extends Fragment {

    private MapView mapView;
    private EditText startAddressInput, endAddressInput;
    private List<EditText> stationInputList = new ArrayList<EditText>();
    private Button drawRouteButton;
    private Button confirmButton;
    private Button addStopBtn;
    private Button removeStopBtn;
    private boolean isDropdownOpen = false;
    private CardView locationDisplay;
    private CardView dropdownContent;
    private LinearLayout stopsContainer;
    private TextView locationText;
    private TextView timeText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        mapView = view.findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(12.75);
        mapView.getController().setCenter(new GeoPoint(45.242, 19.8227));

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

        drawRouteButton.setOnClickListener(v -> {
            String startAddress = startAddressInput.getText().toString().trim();
            String endAddress = endAddressInput.getText().toString().trim();

            if (!startAddress.isEmpty() && !endAddress.isEmpty()) {
                fetchRoute(startAddress, endAddress);
            } else {
                Toast.makeText(requireContext(), "Please enter both addresses", Toast.LENGTH_SHORT).show();
            }
        });

        locationDisplay.setOnClickListener(v -> toggleDropdown());
        confirmButton.setOnClickListener(v -> {
            toggleDropdown();
            displayRouteAsText();
        });
        addStopBtn.setOnClickListener(v -> addStop());
        removeStopBtn.setOnClickListener(v -> removeStop());

        return view;
    }

    private void addStop() {
        EditText stationInput = new EditText(getContext());
        stationInput.setHint("Enter stop location");
        stationInput.setPadding(10, 10, 10, 10);
        stationInput.setBackgroundResource(R.drawable.input_background);
        stationInput.setHint("Enter Station");
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

    private void removeStop(){
        if (stationInputList.isEmpty()){
            return;
        }
        EditText removed = stationInputList.remove(stationInputList.size() - 1);
        stopsContainer.removeView(removed);
    }


    private void toggleDropdown() {
        isDropdownOpen = !isDropdownOpen;
        if (isDropdownOpen) {
            locationDisplay.setVisibility(View.GONE);
            dropdownContent.setVisibility(View.VISIBLE);
        } else {
            locationDisplay.setVisibility(View.VISIBLE);
            dropdownContent.setVisibility(View.GONE);
        }
    }

    private void displayRouteAsText(){
        String startAddress = startAddressInput.getText().toString().trim();
        String endAddress = endAddressInput.getText().toString().trim();
        if(!startAddress.isBlank() && !endAddress.isBlank()){
            StringBuilder sb = new StringBuilder(startAddress);
            for(EditText stationInput : stationInputList){
                String stationText = stationInput.getText().toString().trim();
                sb.append(" → ");
                sb.append(stationText);
            }
            sb.append(" → ");
            sb.append(endAddress);
            locationText.setText(sb.toString());
        }
    }

    private void fetchRoute(String startAddress, String endAddress) {
        new Thread(() -> {
            try {
                GeoPoint startPoint = getCoordinatesFromAddress(startAddress);
                GeoPoint endPoint = getCoordinatesFromAddress(endAddress);

                if (startPoint != null && endPoint != null) {
                    List<GeoPoint> stations = new ArrayList<>();
                    stations.add(startPoint);
                    for(EditText stationInput : stationInputList){
                        String stationText = stationInput.getText().toString().trim();
                        GeoPoint station = getCoordinatesFromAddress(stationText);
                        stations.add(station);
                    }
                    stations.add(endPoint);
                    if (!stations.contains(null)) {
                        List<GeoPoint> routePoints = getRoute(stations);
                        requireActivity().runOnUiThread(() -> drawRoute(routePoints));

                    } else {
                        showToast("Unable to fetch route");
                    }
                } else {
                    showToast("Invalid address provided");
                }
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
                showToast("Error fetching route");
            }
        }).start();
    }

    private GeoPoint getCoordinatesFromAddress(String address) throws IOException, JSONException, InterruptedException {
        try {
        Thread.sleep(1000); // Rate limiting

        String encodedAddress = URLEncoder.encode(address + ", Novi Sad, Serbia", "UTF-8");
        String urlString = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedAddress;

        Log.i("RouteFragment", "Requesting: " + urlString);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "UberCorp/1.0");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.connect();

        int responseCode = conn.getResponseCode();
        Log.i("RouteFragment", "Response code: " + responseCode);

        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            String responseBody = result.toString();
            Log.i("RouteFragment", "Response: " + responseBody);

            JSONArray results = new JSONArray(responseBody);
            if (results.length() > 0) {
                JSONObject resultObj = results.getJSONObject(0);
                double lat = resultObj.getDouble("lat");
                double lon = resultObj.getDouble("lon");
                return new GeoPoint(lat, lon);
            }
        }

        conn.disconnect();
    } catch (Exception e) {
        Log.e("RouteFragment", "Error: " + e.getMessage(), e);
    }
    return null;
    }

    private String buildQueryString(List<GeoPoint> stations){
        StringBuilder sb = new StringBuilder("https://router.project-osrm.org/route/v1/driving/");
        for(GeoPoint station : stations){
            sb.append(station.getLongitude());
            sb.append(",");
            sb.append(station.getLatitude());
            if(station != stations.get(stations.size() - 1)) sb.append(";");
        }
        sb.append("?overview=full&geometries=geojson");
        return sb.toString();
    }

    private List<GeoPoint> getRoute(List<GeoPoint> stations) throws IOException, JSONException {
        try {
            String urlString = buildQueryString(stations);
            Log.i("RouteFragment", "Requesting route: " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.connect();

            int responseCode = conn.getResponseCode();
            Log.i("RouteFragment", "Route response code: " + responseCode);

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                String responseBody = result.toString();
                JSONObject json = new JSONObject(responseBody);
                JSONArray coordinates = json.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONArray("coordinates");

                Double duration = json.getJSONArray("routes")
                        .getJSONObject(0).getDouble("duration") / 60;

                requireActivity().runOnUiThread(() ->
                        timeText.setText("Estimated time: " + duration.intValue() + " minutes")
                );

                List<GeoPoint> routePoints = new ArrayList<>();
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = coordinates.getJSONArray(i);
                    double lon = point.getDouble(0);
                    double lat = point.getDouble(1);
                    routePoints.add(new GeoPoint(lat, lon));
                }

                conn.disconnect();
                return routePoints;
            }

            conn.disconnect();
        } catch (Exception e) {
            Log.e("RouteFragment", "Route error: " + e.getMessage(), e);
        }
        return null;
    }

    private void drawRoute(List<GeoPoint> routePoints) {
        Polyline routeLine = new Polyline();
        routeLine.setPoints(routePoints);
        routeLine.setColor(0xFF0000FF);
        routeLine.setWidth(10.0f);

        mapView.getOverlays().clear();
        mapView.getOverlays().add(routeLine);
        mapView.invalidate();

        // Center the map on the first route point
        if (!routePoints.isEmpty()) {
            IMapController mapController = mapView.getController();
            mapController.setZoom(15.0);
            mapController.setCenter(routePoints.get(0));
        }
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }
}