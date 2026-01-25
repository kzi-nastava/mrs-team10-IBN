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
import com.google.android.material.datepicker.MaterialDatePicker;
import android.widget.EditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RideHistoryFragment extends Fragment {
    private FragmentRideHistoryBinding binding;
    private SimpleDateFormat dateFormat;
    private RideManager rideManager;

    public RideHistoryFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRideHistoryBinding.inflate(inflater, container, false);

        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        EditText editTextStartDate = binding.editTextStartDate;
        EditText editTextEndDate = binding.editTextEndDate;

        Calendar today = Calendar.getInstance();
        editTextStartDate.setText(dateFormat.format(today.getTime()));
        editTextEndDate.setText(dateFormat.format(today.getTime()));

        editTextStartDate.setOnClickListener(v -> showMaterialDatePicker(true));
        editTextEndDate.setOnClickListener(v -> showMaterialDatePicker(false));

        FragmentTransition.to(
                new RideHistoryListFragment(),
                getActivity(),
                false,
                R.id.scroll_rides_list
        );

        return binding.getRoot();
    }

    private void showMaterialDatePicker(boolean isStartDate) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isStartDate ? "Pick start date" : "Pick end date: ")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String isoDate = isoFormat.format(calendar.getTime());

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            String displayDate = displayFormat.format(calendar.getTime());

            if (isStartDate) {
                binding.editTextStartDate.setText(displayDate);
                binding.editTextStartDate.setTag(isoDate);
            } else {
                binding.editTextEndDate.setText(displayDate);
                binding.editTextEndDate.setTag(isoDate);
            }

            String startDate = (String) binding.editTextStartDate.getTag();
            String endDate = (String) binding.editTextEndDate.getTag();

            RideHistoryListFragment fragment = new RideHistoryListFragment();
            Bundle args = new Bundle();
            args.putString("startDate", startDate + "T00:00:00Z");
            args.putString("endDate", endDate + "T23:59:59Z");
            fragment.setArguments(args);

            FragmentTransition.to(
                    fragment,
                    getActivity(),
                    false,
                    R.id.scroll_rides_list
            );
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }


}