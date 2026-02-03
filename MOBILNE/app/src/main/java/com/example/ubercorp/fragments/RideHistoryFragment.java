package com.example.ubercorp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

import com.example.ubercorp.model.SpinnerItem;
import com.example.ubercorp.utils.JwtUtils;
import com.google.android.material.datepicker.MaterialDatePicker;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RideHistoryFragment extends Fragment {
    private FragmentRideHistoryBinding binding;
    private SimpleDateFormat dateFormat;
    private RideManager rideManager;
    private FragmentManager manager;

    public RideHistoryFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRideHistoryBinding.inflate(inflater, container, false);

        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());


        SharedPreferences sharedPref = this.getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        String role = JwtUtils.getRoleFromToken(sharedPref.getString("auth_token", null));
        if(role.equals("administrator")){
            Spinner spinner = binding.sortSpinner;
            ArrayList<SpinnerItem> spinnerOptions = new ArrayList<>();
            spinnerOptions.add(new SpinnerItem("Start (descending)", "start-desc"));
            spinnerOptions.add(new SpinnerItem("Start (ascending)", "start-asc"));
            spinnerOptions.add(new SpinnerItem("End (descending)", "end-desc"));
            spinnerOptions.add(new SpinnerItem("End (ascending)", "end-asc"));
            spinnerOptions.add(new SpinnerItem("Price (descending)", "price-desc"));
            spinnerOptions.add(new SpinnerItem("Price (ascending)", "price-asc"));
            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(this.getContext(), R.layout.spinner_list, spinnerOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Bundle args = new Bundle();
                    RideHistoryListFragment fragment = new RideHistoryListFragment();
                    args.putString("sort", ((SpinnerItem) parent.getItemAtPosition(position)).getValue());
                    fragment.setArguments(args);

                    manager.setFragmentResult("query", args);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

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

        manager = getActivity().getSupportFragmentManager();

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
            if(isStartDate) args.putString("startDate", startDate + "T00:00:00Z");
            else args.putString("endDate", endDate + "T23:59:59Z");
            fragment.setArguments(args);

            manager.setFragmentResult("query", args);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }


}