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

import com.example.ubercorp.R;
import com.example.ubercorp.activities.enums.RideStatus;
import com.example.ubercorp.managers.RouteManager;
import com.example.ubercorp.databinding.FragmentRideDetailsBinding;
import com.example.ubercorp.dto.CoordinateDTO;
import com.example.ubercorp.dto.CreatedUserDTO;
import com.example.ubercorp.dto.GetRideDetailsDTO;
import com.example.ubercorp.dto.RideDTO;
import com.example.ubercorp.managers.RideManager;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

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

                GetRideDetailsDTO dto = response.body();
                RouteManager routeManager = new RouteManager(binding.map, requireContext());
                List<GeoPoint> stations = new ArrayList<>();
                for (CoordinateDTO station : dto.getRoute().getStations()) {
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

            // passengers
            GridLayout passengersLayout = binding.passengers;
            passengersLayout.removeAllViews();

            List<CreatedUserDTO> passengers = ride.getPassengers();

            for (int i = 0; i < passengers.size(); i++) {
                CreatedUserDTO user = passengers.get(i);

                view = LayoutInflater.from(getContext())
                        .inflate(R.layout.passenger, passengersLayout, false);

                ((TextView) view.findViewById(R.id.user_name)).setText(user.getName() + " "+ user.getLastName());
                ((TextView) view.findViewById(R.id.phone_number)).setText(user.getPhone());

                passengersLayout.addView(view);
            }

        }

    }

    private void drawRouteOnMap(List<GeoPoint> routePoints, List<GeoPoint> stations) {
        if (binding.map == null || routePoints == null || routePoints.isEmpty()) return;

        // 1️⃣ Polyline rute
        Polyline routeLine = new Polyline();
        routeLine.setPoints(routePoints);
        routeLine.setColor(0xFF0077CC); // plava
        routeLine.setWidth(10.0f);

        // 2️⃣ Očisti postojeće overlay-e
        binding.map.getOverlays().clear();
        binding.map.getOverlays().add(routeLine);

        // 3️⃣ Dodaj markere na sve stanice
        for (GeoPoint station : stations) {
            Marker marker = new Marker(binding.map);
            marker.setPosition(station);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_location));
            binding.map.getOverlays().add(marker);
        }

        // 4️⃣ Centrira mapu na BoundingBox
        double minLat = routePoints.get(0).getLatitude();
        double maxLat = routePoints.get(0).getLatitude();
        double minLon = routePoints.get(0).getLongitude();
        double maxLon = routePoints.get(0).getLongitude();

        for (GeoPoint p : routePoints) {
            if (p.getLatitude() < minLat) minLat = p.getLatitude();
            if (p.getLatitude() > maxLat) maxLat = p.getLatitude();
            if (p.getLongitude() < minLon) minLon = p.getLongitude();
            if (p.getLongitude() > maxLon) maxLon = p.getLongitude();
        }

        BoundingBox bbox = new BoundingBox(maxLat, maxLon, minLat, minLon);
        binding.map.zoomToBoundingBox(bbox, true, 100); // 100 px padding

        binding.map.invalidate();
    }



    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }

}