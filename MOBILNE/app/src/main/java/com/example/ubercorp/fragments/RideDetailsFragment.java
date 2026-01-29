package com.example.ubercorp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ubercorp.R;
import com.example.ubercorp.activities.enums.RideStatus;
import com.example.ubercorp.dto.FavoriteRouteDTO;
import com.example.ubercorp.managers.RouteManager;
import com.example.ubercorp.databinding.FragmentRideDetailsBinding;
import com.example.ubercorp.dto.CoordinateDTO;
import com.example.ubercorp.dto.CreatedUserDTO;
import com.example.ubercorp.dto.GetRideDetailsDTO;
import com.example.ubercorp.dto.RideDTO;
import com.example.ubercorp.managers.RideManager;
import com.google.android.material.imageview.ShapeableImageView;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RideDetailsFragment extends Fragment {

    private RideDTO ride;
    private RideManager rideManager;
    private FragmentRideDetailsBinding binding;
    private RouteManager routeManager;
    private GetRideDetailsDTO rideDetails;
    private boolean isFavorite = false;

    public RideDetailsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            ride = (RideDTO) getArguments().getParcelable("ride");
        }
        rideManager = new RideManager(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRideDetailsBinding.inflate(inflater, container, false);
        routeManager = new RouteManager(binding.map, requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rideManager.loadRideDetails(ride.getId(), new Callback<GetRideDetailsDTO>() {
            @Override
            public void onResponse(Call<GetRideDetailsDTO> call, Response<GetRideDetailsDTO> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                rideDetails = response.body();

                checkIfFavorite();

                RouteManager routeManager = new RouteManager(binding.map, requireContext());
                List<GeoPoint> stations = new ArrayList<>();
                for (CoordinateDTO station : rideDetails.getRoute().getStations()) {
                    stations.add(new GeoPoint(station.getLat(), station.getLon()));
                }

                new Thread(() -> {
                    List<GeoPoint> routePoints = routeManager.getRoute(stations);
                    requireActivity().runOnUiThread(() -> routeManager.drawRoute(routePoints, stations));
                }).start();
            }

            @Override
            public void onFailure(Call<GetRideDetailsDTO> call, Throwable t) {
            }
        });

        binding.favorites.setOnClickListener(v -> toggleFavorite());

        if (ride != null) {
            binding.startTimeDetail.setText(ride.getStartTime().substring(11,16));
            binding.endTimeDetail.setText(ride.getEndTime().substring(11,16));
            binding.priceDetail.setText(ride.getPrice()+" RSD");
            binding.canceled.setEnabled(false);
            binding.panic.setEnabled(false);
            if (ride.getCancellationReason() != null)
                binding.canceled.setChecked(true);
            if (ride.getStatus() == RideStatus.Panic)
                binding.panic.setChecked(true);

            GridLayout passengersLayout = binding.passengers;
            passengersLayout.removeAllViews();

            List<CreatedUserDTO> passengers = ride.getPassengers();

            for (int i = 0; i < passengers.size(); i++) {
                CreatedUserDTO user = passengers.get(i);

                view = LayoutInflater.from(getContext())
                        .inflate(R.layout.passenger, passengersLayout, false);

                ((TextView) view.findViewById(R.id.user_name)).setText(user.getName() + " "+ user.getLastName());
                ((TextView) view.findViewById(R.id.phone_number)).setText(user.getPhone());
                ShapeableImageView imageView = view.findViewById(R.id.profile_pic);

                String imagePath = user.getImage();

                if (imagePath == null || imagePath.isEmpty()) {
                    imageView.setImageResource(R.drawable.placeholderpfp);
                } else {
                    File imgFile = new File(imagePath);

                    if (imgFile.exists()) {
                        Glide.with(view.getContext())
                                .load(imgFile)
                                .placeholder(R.drawable.placeholderpfp)
                                .error(R.drawable.placeholderpfp)
                                .into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.placeholderpfp);
                    }
                }
                passengersLayout.addView(view);
            }
        }
    }

    private void checkIfFavorite() {
        if (rideDetails == null) return;

        Long routeId = rideDetails.getRoute().getId();

        rideManager.getFavoriteRoutes(new Callback<List<FavoriteRouteDTO>>() {
            @Override
            public void onResponse(Call<List<FavoriteRouteDTO>> call, Response<List<FavoriteRouteDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FavoriteRouteDTO> favoriteRoutes = response.body();

                    isFavorite = false;
                    for (FavoriteRouteDTO favorite : favoriteRoutes) {
                        if (favorite.getRouteDTO().getId().equals(routeId)) {
                            isFavorite = true;
                            break;
                        }
                    }

                    updateFavoriteButton();
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteRouteDTO>> call, Throwable t) {
                isFavorite = false;
                updateFavoriteButton();
            }
        });
    }

    private void toggleFavorite() {
        if (rideDetails == null) {
            Toast.makeText(requireContext(), "Route details not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        Long routeId = rideDetails.getRoute().getId();

        if (isFavorite) {
            rideManager.removeFromFavoritesByRouteId(routeId, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        isFavorite = false;
                        updateFavoriteButton();
                        Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            rideManager.addToFavorites(routeId, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        isFavorite = true;
                        updateFavoriteButton();
                        Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            binding.favorites.setText("❤️");
        } else {
            binding.favorites.setText("🤍");
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}