package com.example.ubercorp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ubercorp.adapters.CurrentRidesAdapter;
import com.example.ubercorp.adapters.DriversAdapter;
import com.example.ubercorp.adapters.PassengersAdapter;
import com.example.ubercorp.api.RideService;
import com.example.ubercorp.api.UserService;
import com.example.ubercorp.databinding.FragmentAdminHomeBinding;
import com.example.ubercorp.dialogs.BlockUserDialog;
import com.example.ubercorp.dialogs.UnblockUserDialog;
import com.example.ubercorp.dto.DriversRidesDTO;
import com.example.ubercorp.dto.PageDTO;
import com.example.ubercorp.dto.UserDTO;
import com.example.ubercorp.api.ApiClient;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHomeFragment extends Fragment {

    private FragmentAdminHomeBinding binding;
    private UserService userService;
    private RideService rideService;
    private String authToken;
    private Call<PageDTO<UserDTO>> driversCall;
    private Call<PageDTO<UserDTO>> passengersCall;
    private Call<PageDTO<DriversRidesDTO>> ridesCall;
    private Call<String> blockUserCall;
    private Call<String> unblockUserCall;

    private DriversAdapter driversAdapter;
    private List<UserDTO> driversList = new ArrayList<>();
    private int driversPageIndex = 0;
    private int driversPageSize = 5;
    private long driversTotalElements = 0;

    private PassengersAdapter passengersAdapter;
    private List<UserDTO> passengersList = new ArrayList<>();
    private int passengersPageIndex = 0;
    private int passengersPageSize = 5;
    private long passengersTotalElements = 0;

    private CurrentRidesAdapter currentRidesAdapter;
    private List<DriversRidesDTO> currentRidesList = new ArrayList<>();
    private int ridesPageIndex = 0;
    private int ridesPageSize = 2;
    private long ridesTotalElements = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPref = getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);

        authToken = "Bearer " + token;

        userService = ApiClient.getInstance().createService(UserService.class);
        rideService = ApiClient.getInstance().createService(RideService.class);

        setupDriversRecyclerView();
        setupPassengersRecyclerView();
        setupCurrentRidesRecyclerView();
        setupTabLayout();
        setupPagination();

        loadDrivers();
        loadPassengers();
        loadCurrentRides("");

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ridesPageIndex = 0;
                loadCurrentRides(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    private void setupDriversRecyclerView() {
        driversAdapter = new DriversAdapter(driversList, new DriversAdapter.OnDriverActionListener() {
            @Override
            public void onBlock(UserDTO driver) {
                showBlockDialog(driver, true);
            }

            @Override
            public void onUnblock(UserDTO driver) {
                showUnblockDialog(driver, true);
            }
        });
        binding.driversRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.driversRecyclerView.setAdapter(driversAdapter);
    }

    private void setupPassengersRecyclerView() {
        passengersAdapter = new PassengersAdapter(passengersList, new PassengersAdapter.OnPassengerActionListener() {
            @Override
            public void onBlock(UserDTO passenger) {
                showBlockDialog(passenger, false);
            }

            @Override
            public void onUnblock(UserDTO passenger) {
                showUnblockDialog(passenger, false);
            }
        });
        binding.passengersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.passengersRecyclerView.setAdapter(passengersAdapter);
    }

    private void setupCurrentRidesRecyclerView(){
        currentRidesAdapter = new CurrentRidesAdapter(currentRidesList);
        binding.ridesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ridesRecyclerView.setAdapter(currentRidesAdapter);
    }

    private void setupTabLayout() {
        binding.usersTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        binding.driversContainer.setVisibility(View.VISIBLE);
                        binding.passengersContainer.setVisibility(View.GONE);
                        binding.currentRidesContainer.setVisibility(View.GONE);
                        break;
                    case 1:
                        binding.driversContainer.setVisibility(View.GONE);
                        binding.currentRidesContainer.setVisibility(View.GONE);
                        binding.passengersContainer.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        binding.currentRidesContainer.setVisibility(View.VISIBLE);
                        binding.passengersContainer.setVisibility(View.GONE);
                        binding.driversContainer.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupPagination() {
        binding.driversPrevButton.setEnabled(false);
        binding.driversNextButton.setEnabled(false);

        binding.passengersPrevButton.setEnabled(false);
        binding.passengersNextButton.setEnabled(false);

        binding.ridesNextButton.setEnabled(false);
        binding.ridesPrevButton.setEnabled(false);

        binding.driversPrevButton.setOnClickListener(v -> {
            if (driversPageIndex > 0) {
                driversPageIndex--;
                loadDrivers();
            }
        });

        binding.driversNextButton.setOnClickListener(v -> {
            if ((driversPageIndex + 1) * driversPageSize < driversTotalElements) {
                driversPageIndex++;
                loadDrivers();
            }
        });

        binding.passengersPrevButton.setOnClickListener(v -> {
            if (passengersPageIndex > 0) {
                passengersPageIndex--;
                loadPassengers();
            }
        });

        binding.passengersNextButton.setOnClickListener(v -> {
            if ((passengersPageIndex + 1) * passengersPageSize < passengersTotalElements) {
                passengersPageIndex++;
                loadPassengers();
            }
        });

        binding.ridesPrevButton.setOnClickListener(v -> {
            if (ridesPageIndex > 0) {
                ridesPageIndex--;
                loadCurrentRides("");
            }
        });

        binding.ridesNextButton.setOnClickListener(v -> {
            if ((ridesPageIndex + 1) * ridesPageSize < ridesTotalElements) {
                ridesPageIndex++;
                loadCurrentRides("");
            }
        });
    }

    private void loadDrivers() {
        if (binding == null) return;

        binding.driversProgressBar.setVisibility(View.VISIBLE);

        if (driversCall != null && !driversCall.isCanceled()) {
            driversCall.cancel();
        }

        driversCall = userService.getDrivers(authToken, driversPageIndex, driversPageSize);
        driversCall.enqueue(new Callback<PageDTO<UserDTO>>() {
            @Override
            public void onResponse(Call<PageDTO<UserDTO>> call, Response<PageDTO<UserDTO>> response) {
                if (binding == null || !isAdded()) return;

                binding.driversProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    driversList.clear();
                    driversList.addAll(response.body().getContent());
                    driversTotalElements = response.body().getTotalElements();
                    driversAdapter.notifyDataSetChanged();
                    updateDriversPaginationButtons();
                } else {
                    Toast.makeText(getContext(), "Failed to load drivers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageDTO<UserDTO>> call, Throwable t) {
                if (binding == null || !isAdded()) return;

                binding.driversProgressBar.setVisibility(View.GONE);
                if (!call.isCanceled()) {
                    Toast.makeText(getContext(), "Error loading drivers: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadPassengers() {
        if (binding == null) return;

        binding.passengersProgressBar.setVisibility(View.VISIBLE);

        if (passengersCall != null && !passengersCall.isCanceled()) {
            passengersCall.cancel();
        }

        passengersCall = userService.getPassengers(authToken, passengersPageIndex, passengersPageSize);
        passengersCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PageDTO<UserDTO>> call, Response<PageDTO<UserDTO>> response) {
                if (binding == null || !isAdded()) return;

                binding.passengersProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    passengersList.clear();
                    passengersList.addAll(response.body().getContent());
                    passengersTotalElements = response.body().getTotalElements();
                    passengersAdapter.notifyDataSetChanged();
                    updatePassengersPaginationButtons();
                } else {
                    Toast.makeText(getContext(), "Failed to load passengers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageDTO<UserDTO>> call, Throwable t) {
                if (binding == null || !isAdded()) return;

                binding.passengersProgressBar.setVisibility(View.GONE);
                if (!call.isCanceled()) {
                    Toast.makeText(getContext(), "Error loading passengers: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadCurrentRides(String search){
        if (binding == null) return;

        binding.ridesProgressBar.setVisibility(View.VISIBLE);

        if (ridesCall != null && !ridesCall.isCanceled()) {
            ridesCall.cancel();
        }

        ridesCall = rideService.getCurrentRides(authToken, ridesPageIndex, ridesPageSize, search);
        ridesCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PageDTO<DriversRidesDTO>> call, Response<PageDTO<DriversRidesDTO>> response) {
                if (binding == null || !isAdded()) return;

                binding.ridesProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentRidesList.clear();
                    currentRidesList.addAll(response.body().getContent());
                    ridesTotalElements = response.body().getTotalElements();
                    currentRidesAdapter.notifyDataSetChanged();
                    updateCurrentRidesPaginationButtons();
                } else {
                    Toast.makeText(getContext(), "Failed to load rides", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageDTO<DriversRidesDTO>> call, Throwable t) {
                if (binding == null || !isAdded()) return;

                binding.ridesProgressBar.setVisibility(View.GONE);
                if (!call.isCanceled()) {
                    Toast.makeText(getContext(), "Error loading rides: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showBlockDialog(UserDTO user, boolean isDriver) {
        BlockUserDialog dialog = new BlockUserDialog(user, isDriver, reason -> {
            Map<String, String> body = new HashMap<>();
            body.put("mail", user.getEmail());
            body.put("reason", reason);

            if (blockUserCall != null && !blockUserCall.isCanceled()) {
                blockUserCall.cancel();
            }

            blockUserCall = userService.blockUser(authToken, body);
            blockUserCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (!isAdded()) return;

                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(),
                                (isDriver ? "Driver" : "Passenger") + " blocked successfully",
                                Toast.LENGTH_SHORT).show();
                        if (isDriver) {
                            loadDrivers();
                        } else {
                            loadPassengers();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to block user",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (!isAdded()) return;

                    if (!call.isCanceled()) {
                        Toast.makeText(getContext(), "Error blocking user: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("AdminHomeFragment","Error blocking user: " + t.getMessage());
                    }
                }
            });
        });
        dialog.show(getChildFragmentManager(), "BlockUserDialog");
    }

    private void showUnblockDialog(UserDTO user, boolean isDriver) {
        UnblockUserDialog dialog = new UnblockUserDialog(user, isDriver, () -> {
            Map<String, String> body = new HashMap<>();
            body.put("mail", user.getEmail());

            if (unblockUserCall != null && !unblockUserCall.isCanceled()) {
                unblockUserCall.cancel();
            }

            unblockUserCall = userService.unblockUser(authToken, body);
            unblockUserCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (!isAdded()) return;

                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(),
                                (isDriver ? "Driver" : "Passenger") + " unblocked successfully",
                                Toast.LENGTH_SHORT).show();
                        if (isDriver) {
                            loadDrivers();
                        } else {
                            loadPassengers();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to unblock user",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (!isAdded()) return;

                    if (!call.isCanceled()) {
                        Toast.makeText(getContext(), "Error unblocking user: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
        dialog.show(getChildFragmentManager(), "UnblockUserDialog");
    }

    private void updateDriversPaginationButtons() {
        if (binding == null) return;

        binding.driversPrevButton.setEnabled(driversPageIndex > 0);
        binding.driversNextButton.setEnabled((driversPageIndex + 1) * driversPageSize < driversTotalElements);
        binding.driversPageInfo.setText(String.format("Page %d of %d",
                driversPageIndex + 1, (driversTotalElements + driversPageSize - 1) / driversPageSize));
    }

    private void updatePassengersPaginationButtons() {
        if (binding == null) return;

        binding.passengersPrevButton.setEnabled(passengersPageIndex > 0);
        binding.passengersNextButton.setEnabled((passengersPageIndex + 1) * passengersPageSize < passengersTotalElements);
        binding.passengersPageInfo.setText(String.format("Page %d of %d",
                passengersPageIndex + 1, (passengersTotalElements + passengersPageSize - 1) / passengersPageSize));
    }

    private void updateCurrentRidesPaginationButtons() {
        if (binding == null) return;

        binding.ridesPrevButton.setEnabled(ridesPageIndex > 0);
        binding.ridesNextButton.setEnabled((ridesPageIndex + 1) * ridesPageSize < ridesTotalElements);
        binding.ridesPageInfo.setText(String.format("Page %d of %d",
                ridesPageIndex + 1, (ridesTotalElements + ridesPageSize - 1) / ridesPageSize));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (driversCall != null && !driversCall.isCanceled()) {
            driversCall.cancel();
        }
        if (passengersCall != null && !passengersCall.isCanceled()) {
            passengersCall.cancel();
        }
        if (ridesCall != null && !ridesCall.isCanceled()) {
            ridesCall.cancel();
        }
        if (blockUserCall != null && !blockUserCall.isCanceled()) {
            blockUserCall.cancel();
        }
        if (unblockUserCall != null && !unblockUserCall.isCanceled()) {
            unblockUserCall.cancel();
        }

        binding = null;
    }
}