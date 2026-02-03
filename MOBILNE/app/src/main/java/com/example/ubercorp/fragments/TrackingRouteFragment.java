package com.example.ubercorp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ubercorp.R;
import com.example.ubercorp.dto.GetRideDetailsDTO;
import com.example.ubercorp.managers.RideManager;
import com.example.ubercorp.managers.RouteManager;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;


public class TrackingRouteFragment extends Fragment {
    private MapView mapView;
    private Long rideID;
    private RouteManager routeManager;
    private RideManager rideManager;
    private GetRideDetailsDTO ride;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracking_route, container, false);
        rideID = getArguments().getLong("RideId");
        mapView = view.findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(12.75);
        mapView.getController().setCenter(new GeoPoint(45.242, 19.8227));

        routeManager = new RouteManager(mapView, this.getContext());
        rideManager = new RideManager(this.getContext());

        //rideManager.getRide(rideID);

        return view;
    }
}