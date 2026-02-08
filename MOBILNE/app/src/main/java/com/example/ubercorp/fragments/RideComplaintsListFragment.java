package com.example.ubercorp.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubercorp.R;
import com.example.ubercorp.adapters.RideComplaintsAdapter;
import com.example.ubercorp.adapters.RideHistoryListAdapter;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.databinding.FragmentRideComplaintsListBinding;
import com.example.ubercorp.databinding.FragmentRideHistoryListBinding;
import com.example.ubercorp.dto.GetComplaintDTO;
import com.example.ubercorp.managers.RideManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideComplaintsListFragment extends ListFragment {
    private RideComplaintsAdapter adapter;
    private FragmentRideComplaintsListBinding binding;
    private List<GetComplaintDTO> complaints = new ArrayList<>();
    private RideManager rideManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rideManager = new RideManager(requireContext());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRideComplaintsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new RideComplaintsAdapter(this.getContext(), complaints);
        setListAdapter(adapter);
        Context context = this.getContext();
        rideManager = new RideManager(this.getContext());
        Long rideID = getArguments().getLong("RideID");
        TextView error = view.findViewById(R.id.errormsg);
        ListView list = view.findViewById(android.R.id.list);
        rideManager.getComplaints(rideID, new Callback<List<GetComplaintDTO>>() {
            @Override
            public void onResponse(Call<List<GetComplaintDTO>> call, Response<List<GetComplaintDTO>> response) {
                if(response.isSuccessful() && !response.body().isEmpty()){
                    adapter.addAll(response.body());
                } else {
                    error.setText("No available complaints");
                    list.setVisibility(GONE);
                    error.setVisibility(VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<List<GetComplaintDTO>> call, Throwable t) {
                error.setText("Check your Internet connection and try again!");
                list.setVisibility(GONE);
                error.setVisibility(VISIBLE);
            }
        });

    }

}