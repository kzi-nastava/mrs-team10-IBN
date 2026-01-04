package com.example.ubercorp.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ubercorp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterDriverFragment extends Fragment {

    // Account info
    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etAddress;
    private TextInputEditText etPhone;
    private TextInputEditText etEmail;

    // Vehicle info
    private RadioGroup rgVehicleType;
    private TextInputEditText etNumberOfSeats;
    private MaterialCheckBox cbBabyTransport;
    private MaterialCheckBox cbPetTransport;

    // Cards and buttons
    private MaterialCardView editAccountCard;
    private MaterialCardView editVehicleCard;
    private Button btnRegister;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_driver, container, false);

        editAccountCard = view.findViewById(R.id.editFormCard);
        editVehicleCard = view.findViewById(R.id.editVehicleCard);

        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etAddress = view.findViewById(R.id.etAddress);
        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);

        rgVehicleType = view.findViewById(R.id.rgVehicleType);
        etNumberOfSeats = view.findViewById(R.id.etNumberOfSeats);
        cbBabyTransport = view.findViewById(R.id.cbBabyTransport);
        cbPetTransport = view.findViewById(R.id.cbPetTransport);

        Button btnOpenAccount = view.findViewById(R.id.btnOpenAccount);
        Button btnOpenVehicle = view.findViewById(R.id.btnOpenVehicle);
        btnRegister = view.findViewById(R.id.btnRegister);

        hideFormButtons(view);

        editAccountCard.setVisibility(View.VISIBLE);
        editVehicleCard.setVisibility(View.GONE);
        btnRegister.setVisibility(View.GONE);

        btnOpenAccount.setOnClickListener(v -> {
            editAccountCard.setVisibility(View.VISIBLE);
            editVehicleCard.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
        });

        btnOpenVehicle.setOnClickListener(v -> {
            if (validateAccountInfo()) {
                editVehicleCard.setVisibility(View.VISIBLE);
                editAccountCard.setVisibility(View.GONE);
                btnRegister.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), "Please fill in all account information first", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(v -> {
            if (validateAllInfo()) {
                registerDriver();
            }
        });

        return view;
    }

    private void hideFormButtons(View view) {
        View btnCancel = view.findViewById(R.id.btnCancel);
        View btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        View btnSendChanges = view.findViewById(R.id.btnSendChanges);

        if (btnCancel != null) btnCancel.setVisibility(View.GONE);
        if (btnSaveChanges != null) btnSaveChanges.setVisibility(View.GONE);
        if (btnSendChanges != null) btnSendChanges.setVisibility(View.GONE);

        View btnCancelVehicle = view.findViewById(R.id.btnCancelVehicle);
        View btnSaveVehicle = view.findViewById(R.id.btnSaveVehicle);

        if (btnCancelVehicle != null) btnCancelVehicle.setVisibility(View.GONE);
        if (btnSaveVehicle != null) btnSaveVehicle.setVisibility(View.GONE);
    }

    private boolean validateAccountInfo() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() ||
                phone.isEmpty() || email.isEmpty()) {
            return false;
        }

        if (!email.contains("@")) {
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateVehicleInfo() {
        String seats = etNumberOfSeats.getText().toString().trim();

        if (seats.isEmpty()) {
            Toast.makeText(getContext(), "Please enter number of seats", Toast.LENGTH_SHORT).show();
            return false;
        }

        int seatCount = Integer.parseInt(seats);
        if (seatCount < 1 || seatCount > 15) {
            Toast.makeText(getContext(), "Number of seats must be between 1 and 15", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateAllInfo() {
        return validateAccountInfo() && validateVehicleInfo();
    }

    private void registerDriver() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        String vehicleType = getSelectedVehicleType();
        String numberOfSeats = etNumberOfSeats.getText().toString().trim();
        boolean babyTransport = cbBabyTransport.isChecked();
        boolean petTransport = cbPetTransport.isChecked();

        Toast.makeText(getContext(), "Driver registered successfully!", Toast.LENGTH_LONG).show();
    }

    private String getSelectedVehicleType() {
        int selectedId = rgVehicleType.getCheckedRadioButtonId();
        if (selectedId == R.id.rbStandard) {
            return "Standard";
        } else if (selectedId == R.id.rbLuxury) {
            return "Luxury";
        } else if (selectedId == R.id.rbVan) {
            return "Van";
        }
        return "Standard";
    }

    public RegisterDriverFragment() {
        // Required empty public constructor
    }

    public static RegisterDriverFragment newInstance() {
        return new RegisterDriverFragment();
    }
}