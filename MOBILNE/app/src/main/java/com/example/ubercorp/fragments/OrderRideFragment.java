package com.example.ubercorp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.example.ubercorp.R;
import com.example.ubercorp.dto.CreateRideDTO;
import com.example.ubercorp.dto.FavoriteRouteDTO;
import com.example.ubercorp.dto.GetCoordinateDTO;
import com.example.ubercorp.dto.PriceDTO;
import com.example.ubercorp.dto.RideOrderResponseDTO;
import com.example.ubercorp.managers.RideManager;
import com.example.ubercorp.managers.RouteManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRideFragment extends Fragment {

    // UI elements
    private MapView mapView;


    private CardView locationDisplay;
    private TextView locationText;
    private TextView timeText;
    private ImageView dropdownArrow;
    private CardView dropdownContent;

    private EditText fromLocationInput;
    private EditText toLocationInput;
    private LinearLayout stopsContainer;
    private Button addStopBtn;

    private RadioButton radioNow;
    private RadioButton radioSchedule;
    private LinearLayout dateTimePicker;
    private EditText dateInput;
    private EditText timeInput;

    private Button confirmBtn;

    private CardView standardCar;
    private CardView luxuryCar;
    private CardView vanCar;

    private CardView shareRideCard;
    private TextView shareRideText;
    private ImageView shareRideArrow;
    private CardView shareRideContent;
    private LinearLayout passengersContainer;
    private Button addPassengerBtn;
    private Button confirmShareBtn;

    private CheckBox babyCheckbox;
    private CheckBox petCheckbox;

    private TextView totalPriceText;
    private Button orderBtn;

    // Managers
    private RouteManager routeManager;
    private RideManager rideManager;

    // Data
    private boolean isDropdownOpen = false;
    private String fromLocation = "";
    private String toLocation = "";
    private List<String> stops = new ArrayList<>();
    private String timeOption = "now";
    private String rideDate = "";
    private String rideTime = "";
    private String selectedCar = "STANDARD";
    private boolean isShareRideOpen = false;
    private List<String> passengerEmails = new ArrayList<>();

    // Route data
    private GeoPoint startPoint;
    private GeoPoint endPoint;
    private List<GeoPoint> stopPoints = new ArrayList<>();
    private List<GeoPoint> routePoints = new ArrayList<>();
    private double calculatedPrice = 0.0;
    private int estimatedDuration = 0;
    private double estimatedDistance = 0.0;
    private Calendar selectedDateTime;
    private Button btnFavorites;

    private boolean isRouteConfirmed = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_ride, container, false);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        initializeViews(view);
        initializeManagers();
        setupListeners();
        loadRouteFromArguments();
        updateLocationDisplay();


        return view;
    }

    private void initializeViews(View view) {
        // Map
        mapView = view.findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(12.75);
        mapView.getController().setCenter(new GeoPoint(45.242, 19.8227));



        // Location display
        locationDisplay = view.findViewById(R.id.locationDisplay);
        locationText = view.findViewById(R.id.locationText);
        timeText = view.findViewById(R.id.timeText);
        dropdownArrow = view.findViewById(R.id.dropdownArrow);
        dropdownContent = view.findViewById(R.id.dropdownContent);

        // Location inputs
        fromLocationInput = view.findViewById(R.id.fromLocationInput);
        toLocationInput = view.findViewById(R.id.toLocationInput);
        stopsContainer = view.findViewById(R.id.stopsContainer);
        addStopBtn = view.findViewById(R.id.addStopBtn);

        // Time selection
        radioNow = view.findViewById(R.id.radioNow);
        radioSchedule = view.findViewById(R.id.radioSchedule);
        dateTimePicker = view.findViewById(R.id.dateTimePicker);
        dateInput = view.findViewById(R.id.dateInput);
        timeInput = view.findViewById(R.id.timeInput);

        confirmBtn = view.findViewById(R.id.confirmBtn);

        // Car options
        standardCar = view.findViewById(R.id.standardCar);
        luxuryCar = view.findViewById(R.id.luxuryCar);
        vanCar = view.findViewById(R.id.vanCar);

        // Share ride
        shareRideCard = view.findViewById(R.id.shareRideCard);
        shareRideText = view.findViewById(R.id.shareRideText);
        shareRideArrow = view.findViewById(R.id.shareRideArrow);
        shareRideContent = view.findViewById(R.id.shareRideContent);
        passengersContainer = view.findViewById(R.id.passengersContainer);
        addPassengerBtn = view.findViewById(R.id.addPassengerBtn);
        confirmShareBtn = view.findViewById(R.id.confirmShareBtn);

        // Checkboxes
        babyCheckbox = view.findViewById(R.id.babyCheckbox);
        petCheckbox = view.findViewById(R.id.petCheckbox);

        // Price and order
        totalPriceText = view.findViewById(R.id.totalPriceText);
        orderBtn = view.findViewById(R.id.orderRideButton);

        // Set initial values
        radioNow.setChecked(true);
        dateTimePicker.setVisibility(View.GONE);
        selectCar("STANDARD");

        selectedDateTime = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateInput.setText(dateFormat.format(selectedDateTime.getTime()));
        timeInput.setText(timeFormat.format(selectedDateTime.getTime()));
        btnFavorites = view.findViewById(R.id.btnFavorites);
    }

    private void initializeManagers() {
        routeManager = new RouteManager(mapView, requireContext());
        rideManager = new RideManager(requireContext());
    }

    private void loadRouteFromArguments() {
        Bundle args = getArguments();
        if (args != null) {
            fromLocation = args.getString("fromLocation", "");
            toLocation = args.getString("toLocation", "");

            estimatedDistance = args.getDouble("estimatedDistance", 0.0);
            estimatedDuration = args.getInt("estimatedDuration", 0);

            fromLocationInput.setText(fromLocation);
            toLocationInput.setText(toLocation);

            ArrayList<String> stopsList = args.getStringArrayList("stops");
            if (stopsList != null) {
                for (String stop : stopsList) {
                    if (!stop.isEmpty()) {
                        stops.add(stop);
                        addStop();
                        int lastIndex = stopsContainer.getChildCount() - 1;
                        if (lastIndex >= 0) {
                            View stopView = stopsContainer.getChildAt(lastIndex);
                            EditText stopInput = stopView.findViewById(R.id.stopInput);
                            stopInput.setText(stop);
                        }
                    }
                }
            }

            if (!fromLocation.isEmpty() && !toLocation.isEmpty()) {
                fetchAndDrawRoute(() -> {
                    isRouteConfirmed = true;
                    orderBtn.setEnabled(true);
                });
            }
        }
    }

    private void setupListeners() {
        locationDisplay.setOnClickListener(v -> toggleDropdown());

        addStopBtn.setOnClickListener(v -> addStop());

        radioNow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                timeOption = "now";
                dateTimePicker.setVisibility(View.GONE);
            }
        });

        radioSchedule.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                timeOption = "schedule";
                dateTimePicker.setVisibility(View.VISIBLE);
            }
        });

        // Date picker
        dateInput.setOnClickListener(v -> showDatePicker());
        dateInput.setFocusable(false);
        dateInput.setClickable(true);

        // Time picker
        timeInput.setOnClickListener(v -> showTimePicker());
        timeInput.setFocusable(false);
        timeInput.setClickable(true);

        confirmBtn.setOnClickListener(v -> confirmSelection());

        standardCar.setOnClickListener(v -> selectCar("STANDARD"));
        luxuryCar.setOnClickListener(v -> selectCar("LUXURY"));
        vanCar.setOnClickListener(v -> selectCar("VAN"));

        shareRideCard.setOnClickListener(v -> toggleShareRide());
        addPassengerBtn.setOnClickListener(v -> addPassenger());
        confirmShareBtn.setOnClickListener(v -> confirmShareRide());

        orderBtn.setOnClickListener(v -> placeOrder());
        btnFavorites.setOnClickListener(v -> showFavoriteRoutesDialog());
    }

    private void showDatePicker() {
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 1);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                R.style.CustomDatePickerTheme,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    dateInput.setText(dateFormat.format(selectedDateTime.getTime()));
                    rideDate = dateFormat.format(selectedDateTime.getTime());

                    validateScheduledDateTime();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                R.style.CustomTimePickerTheme,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    selectedDateTime.set(Calendar.SECOND, 0);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    timeInput.setText(timeFormat.format(selectedDateTime.getTime()));
                    rideTime = timeFormat.format(selectedDateTime.getTime());

                    validateScheduledDateTime();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true
        );

        timePickerDialog.show();
    }

    private boolean validateScheduledDateTime() {
        if (timeOption.equals("now")) {
            return true;
        }

        Calendar now = Calendar.getInstance();
        Calendar scheduledTime = (Calendar) selectedDateTime.clone();

        if (scheduledTime.before(now)) {
            Toast.makeText(requireContext(),
                    "Scheduled time cannot be in the past",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        Calendar maxTime = (Calendar) now.clone();
        maxTime.add(Calendar.HOUR_OF_DAY, 5);

        if (scheduledTime.after(maxTime)) {
            Toast.makeText(requireContext(),
                    "Scheduled time cannot be more than 5 hours from now",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
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

    private void addStop() {
        View stopLayout = getLayoutInflater().inflate(R.layout.item_stop, stopsContainer, false);
        EditText stopInput = stopLayout.findViewById(R.id.stopInput);
        ImageButton removeBtn = stopLayout.findViewById(R.id.removeStopBtn);

        final int stopIndex = stops.size();
        stops.add("");

        stopInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (stopIndex < stops.size()) {
                    stops.set(stopIndex, s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        removeBtn.setOnClickListener(v -> {
            int currentIndex = stopsContainer.indexOfChild(stopLayout);
            if (currentIndex >= 0 && currentIndex < stops.size()) {
                stops.remove(currentIndex);
                stopsContainer.removeView(stopLayout);
            }
        });

        stopsContainer.addView(stopLayout);
    }

    private void confirmSelection() {
        fromLocation = fromLocationInput.getText().toString().trim();
        toLocation = toLocationInput.getText().toString().trim();

        if (fromLocation.isEmpty()) {
            Toast.makeText(requireContext(), "Start address is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (toLocation.isEmpty()) {
            Toast.makeText(requireContext(), "Destination address is required", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < stops.size(); i++) {
            String stop = stops.get(i);
            if (stop != null && !stop.trim().isEmpty()) {
            } else {
                stops.remove(i);
                if (i < stopsContainer.getChildCount()) {
                    stopsContainer.removeViewAt(i);
                }
                i--;
            }
        }

        if (timeOption.equals("now")) {
            timeText.setText("⌛ Leave now");
        } else {
            rideDate = dateInput.getText().toString();
            rideTime = timeInput.getText().toString();

            if (rideDate.isEmpty() || rideTime.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter date and time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!validateScheduledDateTime()) {
                return;
            }

            SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd 'at' HH:mm", Locale.getDefault());
            timeText.setText("⌛ " + displayFormat.format(selectedDateTime.getTime()));
        }

        updateLocationDisplay();
        toggleDropdown();
        fetchAndDrawRoute(() -> {
            isRouteConfirmed = true;
            orderBtn.setEnabled(true);
        });
    }

    private void confirmShareRide() {
        List<String> invalidEmails = new ArrayList<>();

        passengerEmails.removeIf(email -> {
            if (email.isEmpty()) {
                return true;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                invalidEmails.add(email);
                return true;
            }
            return false;
        });

        if (!invalidEmails.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Invalid email format: " + invalidEmails.get(0),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passengerEmails.isEmpty()) {
            String text = "👥 Share a ride (" + passengerEmails.size() +
                    " passenger" + (passengerEmails.size() > 1 ? "s" : "") + ")";
            shareRideText.setText(text);
        } else {
            shareRideText.setText("👥 Share a ride");
        }

        toggleShareRide();
    }

    private void placeOrder() {
        if (!isRouteConfirmed) {
            Toast.makeText(requireContext(), "Please confirm your route first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (calculatedPrice == 0.0) {
            Toast.makeText(requireContext(), "Please wait for price calculation", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fromLocation.isEmpty()) {
            Toast.makeText(requireContext(), "Start address is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (toLocation.isEmpty()) {
            Toast.makeText(requireContext(), "Destination address is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startPoint == null || endPoint == null) {
            try {
                startPoint = getCoordinatesFromAddress(fromLocation);
                endPoint = getCoordinatesFromAddress(toLocation);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Invalid coordinates for start or destination", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (estimatedDistance <= 0) {
            Toast.makeText(requireContext(), "Invalid distance calculated", Toast.LENGTH_SHORT).show();
            return;
        }

        if (estimatedDuration <= 0) {
            Toast.makeText(requireContext(), "Invalid duration calculated", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCar == null || selectedCar.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a vehicle type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (timeOption.equals("schedule")) {
            if (rideDate.isEmpty() || rideTime.isEmpty()) {
                Toast.makeText(requireContext(), "Scheduled date and time are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!validateScheduledDateTime()) {
                return;
            }
        }

        CreateRideDTO rideDTO = buildRideDTO();

        if (rideDTO.getStartAddress() == null || rideDTO.getDestinationAddress() == null) {
            Toast.makeText(requireContext(), "Invalid address data", Toast.LENGTH_SHORT).show();
            return;
        }


        orderBtn.setEnabled(false);
        orderBtn.setText("Ordering...");

        rideManager.createRide(rideDTO, new Callback<RideOrderResponseDTO>() {
            @Override
            public void onResponse(Call<RideOrderResponseDTO> call, Response<RideOrderResponseDTO> response) {
                if (orderBtn != null) {
                    orderBtn.setEnabled(true);
                    orderBtn.setText("Order Ride");
                }

                if (response.code() == 201 && response.body() != null) {
                    showOrderConfirmation(response.body());
                } else if (response.code() == 204) {
                    Toast.makeText(requireContext(),
                            "No available drivers at the moment.",
                            Toast.LENGTH_LONG).show();
                } else if (response.code() == 403) {
                    if (response.body() != null && response.body().getStatus() != null) {
                        Toast.makeText(requireContext(),
                                "Account blocked: " + response.body().getStatus(),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(),
                                "Your account is blocked.",
                                Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 400) {
                    Toast.makeText(requireContext(),
                            "Invalid ride data. Please check all fields.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireContext(),
                            "Failed to create ride.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RideOrderResponseDTO> call, Throwable t) {
                if (orderBtn != null) {
                    orderBtn.setEnabled(true);
                    orderBtn.setText("Order Ride");
                }
                Log.e("OrderRideFragment", "Order failed: " + t.getMessage());
                Toast.makeText(requireContext(), "Error creating ride: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private CreateRideDTO buildRideDTO() {
        CreateRideDTO rideDTO = new CreateRideDTO();

        if (startPoint != null && !fromLocation.isEmpty()) {
            rideDTO.setStartAddress(new GetCoordinateDTO(fromLocation, startPoint.getLatitude(), startPoint.getLongitude()));
        }

        if (endPoint != null && !toLocation.isEmpty()) {
            rideDTO.setDestinationAddress(new GetCoordinateDTO(toLocation, endPoint.getLatitude(), endPoint.getLongitude()));
        }

        List<GetCoordinateDTO> stopDTOs = new ArrayList<>();
        for (int i = 0; i < stopPoints.size() && i < stops.size(); i++) {
            GeoPoint point = stopPoints.get(i);
            String stopAddress = stops.get(i);
            if (point != null && stopAddress != null && !stopAddress.trim().isEmpty()) {
                stopDTOs.add(new GetCoordinateDTO(stopAddress, point.getLatitude(), point.getLongitude()));
            }
        }
        rideDTO.setStops(stopDTOs);

        List<String> validEmails = new ArrayList<>();
        for (String email : passengerEmails) {
            if (email != null && !email.trim().isEmpty() &&
                    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                validEmails.add(email.trim());
            }
        }

        rideDTO.setPassengerEmails(validEmails);

        rideDTO.setVehicleType(selectedCar != null && !selectedCar.isEmpty() ? selectedCar : "STANDARD");

        rideDTO.setBabySeat(babyCheckbox != null && babyCheckbox.isChecked());
        rideDTO.setPetFriendly(petCheckbox != null && petCheckbox.isChecked());

        if (timeOption.equals("schedule") && !rideDate.isEmpty() && !rideTime.isEmpty()) {
            String scheduledDateTime = rideDate + " " + rideTime + ":00";
            rideDTO.setScheduled(scheduledDateTime);
        } else {
            rideDTO.setScheduled(null);
        }

        rideDTO.setDistance(estimatedDistance > 0 ? estimatedDistance : 0.0);
        rideDTO.setEstimatedDuration(estimatedDuration > 0 ? estimatedDuration : 0);

        rideDTO.setPrice(calculatedPrice >= 0 ? calculatedPrice : 0.0);

        return rideDTO;
    }

    private void updateLocationDisplay() {
        List<String> locationParts = new ArrayList<>();

        if (!fromLocation.isEmpty()) {
            locationParts.add(fromLocation);
        }

        for (String stop : stops) {
            if (!stop.isEmpty()) {
                locationParts.add(stop);
            }
        }

        if (!toLocation.isEmpty()) {
            locationParts.add(toLocation);
        }

        locationText.setText(String.join(" → ", locationParts));
    }

    private void selectCar(String carType) {
        selectedCar = carType;

        int defaultColor = ContextCompat.getColor(requireContext(), R.color.car_default);
        int selectedColor = ContextCompat.getColor(requireContext(), R.color.car_selected);

        standardCar.setCardBackgroundColor(defaultColor);
        luxuryCar.setCardBackgroundColor(defaultColor);
        vanCar.setCardBackgroundColor(defaultColor);

        switch (carType) {
            case "STANDARD":
                standardCar.setCardBackgroundColor(selectedColor);
                break;
            case "LUXURY":
                luxuryCar.setCardBackgroundColor(selectedColor);
                break;
            case "VAN":
                vanCar.setCardBackgroundColor(selectedColor);
                break;
        }

        calculatePrice();
    }

    private void toggleShareRide() {
        isShareRideOpen = !isShareRideOpen;
        if (isShareRideOpen) {
            shareRideCard.setVisibility(View.GONE);
            shareRideContent.setVisibility(View.VISIBLE);
        } else {
            shareRideCard.setVisibility(View.VISIBLE);
            shareRideContent.setVisibility(View.GONE);
        }
    }

    private void addPassenger() {
        View passengerLayout = getLayoutInflater().inflate(R.layout.item_passenger, passengersContainer, false);
        EditText emailInput = passengerLayout.findViewById(R.id.passengerEmailInput);
        ImageButton removeBtn = passengerLayout.findViewById(R.id.removePassengerBtn);

        final int passengerIndex = passengerEmails.size();
        passengerEmails.add("");

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (passengerIndex < passengerEmails.size()) {
                    passengerEmails.set(passengerIndex, s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        removeBtn.setOnClickListener(v -> {
            int currentIndex = passengersContainer.indexOfChild(passengerLayout);
            if (currentIndex >= 0 && currentIndex < passengerEmails.size()) {
                passengerEmails.remove(currentIndex);
                passengersContainer.removeView(passengerLayout);
            }
        });

        passengersContainer.addView(passengerLayout);
    }

    private void fetchAndDrawRoute(Runnable onComplete) {
        new Thread(() -> {
            try {
                startPoint = getCoordinatesFromAddress(fromLocation);
                endPoint = getCoordinatesFromAddress(toLocation);

                stopPoints.clear();
                for (String stop : stops) {
                    if (!stop.isEmpty()) {
                        GeoPoint stopPoint = getCoordinatesFromAddress(stop);
                        if (stopPoint != null) stopPoints.add(stopPoint);
                    }
                }

                if (startPoint != null && endPoint != null) {
                    List<GeoPoint> allPoints = new ArrayList<>();
                    allPoints.add(startPoint);
                    allPoints.addAll(stopPoints);
                    allPoints.add(endPoint);

                    routePoints = routeManager.getRoute(allPoints);

                    requireActivity().runOnUiThread(() -> {
                        if (routePoints != null && !routePoints.isEmpty()) {
                            routeManager.drawRoute(routePoints, allPoints);

                            estimatedDistance = routeManager.getEstimatedDistance();
                            estimatedDuration = routeManager.getEstimatedDuration();

                            calculatePrice();
                        } else {
                            Toast.makeText(requireContext(), "Unable to fetch route", Toast.LENGTH_SHORT).show();
                        }
                        if (onComplete != null) onComplete.run();
                    });
                } else {
                    if (onComplete != null) requireActivity().runOnUiThread(onComplete);
                }
            } catch (Exception e) {
                Log.e("OrderRideFragment", "Error: " + e.getMessage(), e);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error fetching route", Toast.LENGTH_SHORT).show();
                    if (onComplete != null) onComplete.run();
                });
            }
        }).start();
    }

    private GeoPoint getCoordinatesFromAddress(String address) throws Exception {
        Thread.sleep(1000);

        String fullAddress = address;

        String lower = address.toLowerCase();
        if (!lower.contains("novi sad")) {
            fullAddress += ", Novi Sad";
        }
        if (!lower.contains("serbia")) {
            fullAddress += ", Serbia";
        }

        String encodedAddress = URLEncoder.encode(fullAddress, "UTF-8");
        String urlString = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedAddress;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "UberCorp/1.0");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.connect();

        if (conn.getResponseCode() == 200) {
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
                conn.disconnect();
                return new GeoPoint(lat, lon);
            }
        }

        conn.disconnect();
        return null;
    }

    private void calculatePrice() {
        if (startPoint == null || endPoint == null || routePoints.isEmpty()) {
            return;
        }

        CreateRideDTO rideDTO = buildRideDTO();
        rideDTO.setScheduled(null);

        rideManager.calculatePrice(rideDTO, new Callback<PriceDTO>() {
            @Override
            public void onResponse(Call<PriceDTO> call, Response<PriceDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    calculatedPrice = response.body().getPrice();
                    totalPriceText.setText(String.format(Locale.getDefault(), "Total price: %.2f RSD", calculatedPrice));
                } else {
                    Toast.makeText(requireContext(), "Failed to calculate price", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PriceDTO> call, Throwable t) {
                Log.e("OrderRideFragment", "Price calculation failed: " + t.getMessage());
                Toast.makeText(requireContext(), "Error calculating price", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showOrderConfirmation(RideOrderResponseDTO response) {
        StringBuilder message = new StringBuilder("Ride ordered successfully!\n\n");
        message.append("Price: ").append(String.format(Locale.getDefault(), "%.2f RSD", response.getPrice())).append("\n");

            message.append("\nDriver: ").append(response.getDriverName()).append("\n");
            message.append("Phone: ").append(response.getDriverPhone()).append("\n");
            message.append("Vehicle: ").append(response.getVehicleModel()).append("\n");

            if (response.getEstimatedPickupMinutes() != -1L) {
                message.append("Estimated pickup: ").append(response.getEstimatedPickupMinutes()).append(" minutes");
            }

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Ride Confirmation")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .show();
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

    private void showFavoriteRoutesDialog() {
        FavoriteRoutesDialog dialog = FavoriteRoutesDialog.newInstance();

        dialog.setLoading(true);
        dialog.show(getParentFragmentManager(), FavoriteRoutesDialog.TAG);

        dialog.setOnRouteSelectedListener(this::loadRouteIntoForm);

        loadFavoriteRoutes(dialog);
    }

    private void loadFavoriteRoutes(FavoriteRoutesDialog dialog) {
        rideManager.getFavoriteRoutes(new Callback<List<FavoriteRouteDTO>>() {
            @Override
            public void onResponse(Call<List<FavoriteRouteDTO>> call,
                                   Response<List<FavoriteRouteDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dialog.setLoading(false);
                    dialog.setRoutes(response.body());
                } else {
                    dialog.setLoading(false);
                    Toast.makeText(requireContext(),
                            "Failed to load favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteRouteDTO>> call, Throwable t) {
                dialog.setLoading(false);
                Toast.makeText(requireContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRouteIntoForm(FavoriteRouteDTO route) {
        if (route.getRouteDTO() == null ||
                route.getRouteDTO().getStations() == null ||
                route.getRouteDTO().getStations().isEmpty()) {
            Toast.makeText(requireContext(), "Invalid route data", Toast.LENGTH_SHORT).show();
            return;
        }

        List<GetCoordinateDTO> stations = route.getRouteDTO().getStations();

        stops.clear();
        stopsContainer.removeAllViews();
        stopPoints.clear();

        GetCoordinateDTO startStation = stations.get(0);
        fromLocation = startStation.getAddress();
        fromLocationInput.setText(fromLocation);
        startPoint = new GeoPoint(startStation.getLat(), startStation.getLon());

        GetCoordinateDTO endStation = stations.get(stations.size() - 1);
        toLocation = endStation.getAddress();
        toLocationInput.setText(toLocation);
        endPoint = new GeoPoint(endStation.getLat(), endStation.getLon());

        for (int i = 1; i < stations.size() - 1; i++) {
            GetCoordinateDTO stopStation = stations.get(i);
            String stopAddress = stopStation.getAddress();

            stops.add(stopAddress);
            stopPoints.add(new GeoPoint(stopStation.getLat(), stopStation.getLon()));

            addStop();

            int lastIndex = stopsContainer.getChildCount() - 1;
            if (lastIndex >= 0) {
                View stopView = stopsContainer.getChildAt(lastIndex);
                EditText stopInput = stopView.findViewById(R.id.stopInput);
                stopInput.setText(stopAddress);
            }
        }

        updateLocationDisplay();

        drawRouteFromPoints();
    }

    private void drawRouteFromPoints() {
        new Thread(() -> {
            try {
                List<GeoPoint> allPoints = new ArrayList<>();
                allPoints.add(startPoint);
                allPoints.addAll(stopPoints);
                allPoints.add(endPoint);

                routePoints = routeManager.getRoute(allPoints);

                requireActivity().runOnUiThread(() -> {
                    if (routePoints != null && !routePoints.isEmpty()) {
                        routeManager.drawRoute(routePoints, allPoints);

                        estimatedDistance = routeManager.getEstimatedDistance();
                        estimatedDuration = routeManager.getEstimatedDuration();

                        calculatePrice();

                        isRouteConfirmed = true;
                        orderBtn.setEnabled(true);
                    } else {
                        Toast.makeText(requireContext(), "Unable to draw route", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e("OrderRideFragment", "Error drawing route: " + e.getMessage(), e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error drawing route", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}