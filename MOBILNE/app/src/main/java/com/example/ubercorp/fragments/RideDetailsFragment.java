package com.example.ubercorp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.ubercorp.R;
import com.example.ubercorp.databinding.FragmentRideDetailsBinding;
import com.example.ubercorp.dto.RideDTO;
import com.example.ubercorp.model.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class RideDetailsFragment extends Fragment {

    private RideDTO ride;

    private FragmentRideDetailsBinding binding;

    public RideDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            ride = (RideDTO) getArguments().getParcelable("ride");
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
            //binding.startTimeDetail.setText(sdf.format(ride.getStart()));
            //binding.endTimeDetail.setText(sdf.format(ride.getEstimatedTimeArrival()));
            binding.priceDetail.setText(ride.getPrice()+" RSD");
            // passengers
            GridLayout passengersLayout = binding.passengers;
            passengersLayout.removeAllViews();

            List<User> passengers = ride.getPassengers();

            for (int i = 0; i < passengers.size(); i++) {
                User user = passengers.get(i);

                view = LayoutInflater.from(getContext())
                        .inflate(R.layout.passenger, passengersLayout, false);

                ((TextView) view.findViewById(R.id.user_name)).setText(user.getName());
                ((TextView) view.findViewById(R.id.phone_number)).setText(user.getPhoneNumber());

                passengersLayout.addView(view);
            }
        }

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }

}