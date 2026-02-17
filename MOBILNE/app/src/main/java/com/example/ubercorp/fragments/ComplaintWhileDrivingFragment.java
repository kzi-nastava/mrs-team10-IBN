package com.example.ubercorp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.ComplaintService;
import com.example.ubercorp.api.ReviewService;
import com.example.ubercorp.databinding.FragmentComplaintWhileDrivingBinding;
import com.example.ubercorp.dto.CreateReportDTO;
import com.example.ubercorp.dto.CreateReviewDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintWhileDrivingFragment extends Fragment {

    public ComplaintWhileDrivingFragment() {}
    private Long rideId;
    private ComplaintService complaintService;
    private FragmentComplaintWhileDrivingBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentComplaintWhileDrivingBinding.inflate(inflater, container, false);
        if(getArguments()!= null) rideId = getArguments().getLong("RideID");
        complaintService = ApiClient.getInstance().createService(ComplaintService.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button submitButton = binding.reportButton;
        EditText commentEditText = binding.typeHere;

        submitButton.setOnClickListener(v -> {
            String comment = commentEditText.getText().toString();
            CreateReportDTO report = new CreateReportDTO(rideId,comment);
            SharedPreferences sharedPref = requireContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
            String token = sharedPref.getString("auth_token", null);
            Call<CreateReportDTO> call = complaintService.createReport("Bearer " + token, report);

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<CreateReportDTO> call, Response<CreateReportDTO> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(),
                                "You reported successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(),
                                "Error: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CreateReportDTO> call, Throwable t) {
                    Toast.makeText(requireContext(),
                            "Network error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

        @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}