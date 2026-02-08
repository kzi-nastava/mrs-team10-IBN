package com.example.ubercorp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ubercorp.R;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.DriverService;
import com.example.ubercorp.api.UserService;
import com.example.ubercorp.dto.*;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterDriverFragment extends Fragment {

    // Account info
    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etAddress;
    private TextInputEditText etPhone;
    private TextInputEditText etEmail;

    // Vehicle info
    private RadioGroup rgVehicleType;
    private TextInputEditText etNumberOfSeats, etModel, etPlate;
    private MaterialCheckBox cbBabyTransport;
    private MaterialCheckBox cbPetTransport;

    // Cards and buttons
    private MaterialCardView editAccountCard;
    private MaterialCardView editVehicleCard;
    private Button btnRegister;

    private DriverService driverService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_driver, container, false);

        driverService = ApiClient.getInstance().createService(DriverService.class);

        editAccountCard = view.findViewById(R.id.editFormCard);
        editVehicleCard = view.findViewById(R.id.editVehicleCard);

        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etAddress = view.findViewById(R.id.etAddress);
        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);

        etModel = view.findViewById(R.id.etVehicleModel);
        etPlate = view.findViewById(R.id.etLicensePlate);
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
        String model = etModel.getText().toString().trim();
        String plate = etPlate.getText().toString().trim();
        String seats = etNumberOfSeats.getText().toString().trim();

        if (model.isEmpty() || plate.isEmpty() || seats.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all vehicle information", Toast.LENGTH_SHORT).show();
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

    private String getToken() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        return sharedPref.getString("auth_token", null);
    }
    private void registerDriver() {
        btnRegister.setEnabled(false);

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        String model = etModel.getText().toString().trim();
        String plate = etPlate.getText().toString().trim();
        int numberOfSeats = Integer.parseInt(etNumberOfSeats.getText().toString().trim());
        boolean babyTransport = cbBabyTransport.isChecked();
        boolean petTransport = cbPetTransport.isChecked();

        VehicleTypeDTO vehicleTypeDTO = getSelectedVehicleType();

        AccountDTO accountDTO = new AccountDTO(email);
        CreateUserDTO createUserDTO = new CreateUserDTO(firstName, lastName, address, phone, null);
        VehicleDTO vehicleDTO = new VehicleDTO(vehicleTypeDTO, model, plate, numberOfSeats, babyTransport, petTransport);
        CreateDriverDTO createDriverDTO = new CreateDriverDTO(accountDTO, createUserDTO, vehicleDTO);

        Call<DriverDTO> call = driverService.register("Bearer " + getToken(), createDriverDTO);
        call.enqueue(new Callback<DriverDTO>() {
            @Override
            public void onResponse(Call<DriverDTO> call, Response<DriverDTO> response) {
                btnRegister.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Driver registered successfully!", Toast.LENGTH_LONG).show();

                    clearForm();

                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } else {
                    String errorMessage = "Registration failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DriverDTO> call, Throwable t) {
                btnRegister.setEnabled(true);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private VehicleTypeDTO getSelectedVehicleType() {
        int selectedId = rgVehicleType.getCheckedRadioButtonId();

        if (selectedId == R.id.rbStandard) {
            return new VehicleTypeDTO(0L, "STANDARD", 0.0);
        } else if (selectedId == R.id.rbLuxury) {
            return new VehicleTypeDTO(0L, "LUXURY", 0.0);
        } else if (selectedId == R.id.rbVan) {
            return new VehicleTypeDTO(0L, "VAN", 0.0);
        }

        return new VehicleTypeDTO(0L, "STANDARD", 0.0);
    }

    private void clearForm() {
        etFirstName.setText("");
        etLastName.setText("");
        etAddress.setText("");
        etPhone.setText("");
        etEmail.setText("");
        etModel.setText("");
        etPlate.setText("");
        etNumberOfSeats.setText("");
        cbBabyTransport.setChecked(false);
        cbPetTransport.setChecked(false);
        rgVehicleType.clearCheck();

        editAccountCard.setVisibility(View.VISIBLE);
        editVehicleCard.setVisibility(View.GONE);
        btnRegister.setVisibility(View.GONE);
    }

    public RegisterDriverFragment() {
    }

    public static RegisterDriverFragment newInstance() {
        return new RegisterDriverFragment();
    }
}