package com.example.ubercorp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ubercorp.R;


public class RideComplaintsFragment extends Fragment {
    private Long rideID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments() != null) rideID = getArguments().getLong("RideID");
        Fragment rideComplaintsListFragment = new RideComplaintsListFragment();
        rideComplaintsListFragment.setArguments(getArguments());
        FragmentTransition.to(
                rideComplaintsListFragment,
                getActivity(),
                false,
                R.id.scroll_ride_complaints
        );

        return inflater.inflate(R.layout.fragment_ride_complaints, container, false);
    }
}