package com.example.ubercorp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ubercorp.R;


public class RideReviewsFragment extends Fragment {
    private Long rideID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments() != null) rideID = getArguments().getLong("RideID");
        Fragment rideReviewsListFragment = new RideReviewsListFragment();
        rideReviewsListFragment.setArguments(getArguments());
        FragmentTransition.to(
                rideReviewsListFragment,
                getActivity(),
                false,
                R.id.scroll_ride_reviews
        );

        return inflater.inflate(R.layout.fragment_ride_reviews, container, false);
    }
}