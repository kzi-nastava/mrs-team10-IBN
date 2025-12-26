package com.example.ubercorp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ubercorp.databinding.FragmentRatingDriverAndVehicleBinding;
import com.example.ubercorp.R;

import java.util.Arrays;
import java.util.List;

public class RatingDriverAndVehicle extends Fragment {

    private FragmentRatingDriverAndVehicleBinding binding;

    private int driverRating = 0;
    private int vehicleRating = 0;

    private List<ImageView> driverStars;
    private List<ImageView> vehicleStars;

    public RatingDriverAndVehicle() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRatingDriverAndVehicleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        driverStars = Arrays.asList(
                binding.driverStar1,
                binding.driverStar2,
                binding.driverStar3,
                binding.driverStar4,
                binding.driverStar5
        );

        vehicleStars = Arrays.asList(
                binding.vehicleStar1,
                binding.vehicleStar2,
                binding.vehicleStar3,
                binding.vehicleStar4,
                binding.vehicleStar5
        );

        for (int i = 0; i < driverStars.size(); i++) {
            final int index = i;
            driverStars.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    driverRating = index + 1;
                    updateStars(driverStars, driverRating);
                }
            });
        }

        for (int i = 0; i < vehicleStars.size(); i++) {
            final int index = i;
            vehicleStars.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vehicleRating = index + 1;
                    updateStars(vehicleStars, vehicleRating);
                }
            });
        }

        Button submitButton = binding.reportButton;
        EditText commentEditText = binding.typeHere;

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentEditText.getText().toString();
                Log.d("Rating", "Driver: " + driverRating + ", Vehicle: " + vehicleRating + ", Comment: " + comment);

            }
        });
    }

    private void updateStars(List<ImageView> stars, int rating) {
        for (int i = 0; i < stars.size(); i++) {
            if (i < rating) {
                stars.get(i).setImageResource(R.drawable.ic_star_filled);
            } else {
                stars.get(i).setImageResource(R.drawable.ic_star_border);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
