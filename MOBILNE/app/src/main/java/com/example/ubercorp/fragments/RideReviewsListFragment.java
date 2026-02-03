package com.example.ubercorp.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.example.ubercorp.R;
import com.example.ubercorp.adapters.RideReviewsAdapter;
import com.example.ubercorp.databinding.FragmentRideReviewsListBinding;
import com.example.ubercorp.dto.GetReviewDTO;
import com.example.ubercorp.managers.RideManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RideReviewsListFragment extends ListFragment {

    private RideReviewsAdapter adapter;
    private FragmentRideReviewsListBinding binding;
    private List<GetReviewDTO> reviews = new ArrayList<>();
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
        binding = FragmentRideReviewsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new RideReviewsAdapter(this.getContext(), reviews);
        setListAdapter(adapter);
        Context context = this.getContext();
        rideManager = new RideManager(this.getContext());
        Long rideID = getArguments().getLong("RideID");
        TextView error = view.findViewById(R.id.errormsg);
        ListView list = view.findViewById(android.R.id.list);
        rideManager.getReviews(rideID, new Callback<List<GetReviewDTO>>() {
            @Override
            public void onResponse(Call<List<GetReviewDTO>> call, Response<List<GetReviewDTO>> response) {
                if(response.isSuccessful() && !response.body().isEmpty()){
                    adapter.addAll(response.body());
                } else {
                    error.setText("No available reviews");
                    list.setVisibility(GONE);
                    error.setVisibility(VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<List<GetReviewDTO>> call, Throwable t) {
                error.setText("Check your Internet connection and try again!");
                list.setVisibility(GONE);
                error.setVisibility(VISIBLE);
            }
        });

    }
}