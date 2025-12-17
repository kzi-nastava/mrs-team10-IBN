package com.example.ubercorp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.ubercorp.databinding.FragmentRideDetailsBinding;
import com.example.ubercorp.model.Ride;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class RideDetailsFragment extends Fragment {

    private Ride ride;

    private FragmentRideDetailsBinding binding;

    public RideDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            ride = (Ride) getArguments().getParcelable("ride");
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRideDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ride != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            binding.startTimeDetail.setText(sdf.format(ride.getStart()));
            binding.endTimeDetail.setText(sdf.format(ride.getEnd()));
            binding.priceDetail.setText(ride.getPrice()+" RSD");
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }

}