package com.example.ubercorp.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.ArrayList;
import java.util.List;
import com.example.ubercorp.R;

public class OrderRideFragment extends Fragment {

    // UI elements
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

    // Data
    private boolean isDropdownOpen = false;
    private String fromLocation = "Kopernikova 23";
    private String toLocation = "Železnička stanica";
    private List<String> stops = new ArrayList<>();
    private String timeOption = "now";
    private String rideDate = "";
    private String rideTime = "";
    private String selectedCar = "standard";
    private boolean isShareRideOpen = false;
    private List<String> passengerEmails = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_ride, container, false);

        initializeViews(view);
        setupListeners();
        updateLocationDisplay();
        calculatePrice();

        return view;
    }

    private void initializeViews(View view) {
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
        orderBtn = view.findViewById(R.id.orderBtn);

        // Set initial values
        fromLocationInput.setText(fromLocation);
        toLocationInput.setText(toLocation);
        radioNow.setChecked(true);
        dateTimePicker.setVisibility(View.GONE);
    }

    private void setupListeners() {
        // Location dropdown toggle
        locationDisplay.setOnClickListener(v -> toggleDropdown());

        // Add stop button
        addStopBtn.setOnClickListener(v -> addStop());

        // Time options
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

        // Confirm button
        confirmBtn.setOnClickListener(v -> confirmSelection());

        // Car selection
        standardCar.setOnClickListener(v -> selectCar("standard"));
        luxuryCar.setOnClickListener(v -> selectCar("luxury"));
        vanCar.setOnClickListener(v -> selectCar("van"));

        // Share ride
        shareRideCard.setOnClickListener(v -> toggleShareRide());
        addPassengerBtn.setOnClickListener(v -> addPassenger());
        confirmShareBtn.setOnClickListener(v -> confirmShareRide());

        // Order button
        orderBtn.setOnClickListener(v -> placeOrder());

        // Checkboxes for price calculation
        babyCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> calculatePrice());
        petCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> calculatePrice());
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
                calculatePrice();
            }
        });

        stopsContainer.addView(stopLayout);
    }

    private void confirmSelection() {
        fromLocation = fromLocationInput.getText().toString();
        toLocation = toLocationInput.getText().toString();

        if (timeOption.equals("now")) {
            timeText.setText("⌛ Leave now");
        } else {
            rideDate = dateInput.getText().toString();
            rideTime = timeInput.getText().toString();
            timeText.setText("⌛ " + rideDate + " at " + rideTime);
        }

        updateLocationDisplay();
        toggleDropdown();
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

        // Reset all backgrounds
        int defaultColor = ContextCompat.getColor(requireContext(), R.color.car_default);
        int selectedColor = ContextCompat.getColor(requireContext(), R.color.car_selected);

        standardCar.setCardBackgroundColor(defaultColor);
        luxuryCar.setCardBackgroundColor(defaultColor);
        vanCar.setCardBackgroundColor(defaultColor);

        // Highlight selected
        switch (carType) {
            case "standard":
                standardCar.setCardBackgroundColor(selectedColor);
                break;
            case "luxury":
                luxuryCar.setCardBackgroundColor(selectedColor);
                break;
            case "van":
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

    private void confirmShareRide() {
        // Remove empty emails
        passengerEmails.removeIf(String::isEmpty);

        if (!passengerEmails.isEmpty()) {
            String text = "👥 Share a ride (" + passengerEmails.size() +
                    " passenger" + (passengerEmails.size() > 1 ? "s" : "") + ")";
            shareRideText.setText(text);
        } else {
            shareRideText.setText("👥 Share a ride");
        }

        toggleShareRide();
        calculatePrice();
    }

    private void calculatePrice() {
    }

    private void placeOrder() {
        boolean hasBaby = babyCheckbox.isChecked();
        boolean hasPet = petCheckbox.isChecked();

        String message = "Order placed!\nFrom: " + fromLocation +
                "\nTo: " + toLocation +
                "\nCar: " + selectedCar;

        if (hasBaby) {
            message += "\nWith baby seat";
        }
        if (hasPet) {
            message += "\nPet-friendly";
        }

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}