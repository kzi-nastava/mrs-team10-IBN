package com.example.ubercorp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.ubercorp.R;
import com.example.ubercorp.databinding.FragmentRideHistoryBinding;
import com.example.ubercorp.dto.GetRideDTO;
import com.example.ubercorp.dto.RideDTO;
import com.example.ubercorp.managers.RideManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideHistoryFragment extends Fragment {

    public static ArrayList<GetRideDTO> rides = new ArrayList<>();
    private FragmentRideHistoryBinding binding;

    private RideManager rideManager;


    public RideHistoryFragment() {}
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRideHistoryBinding.inflate(inflater, container, false);

        FragmentTransition.to(
                new RideHistoryListFragment(),
                getActivity(),
                false,
                R.id.scroll_rides_list
        );

        return binding.getRoot();
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }


}