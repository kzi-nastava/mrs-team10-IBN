package com.example.ubercorp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ubercorp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageUsersFragment extends Fragment {
    private Button registerDriverBtn;

    public static ManageUsersFragment newInstance() {
        return new ManageUsersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_users, container, false);
        initializeViews(view);
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        registerDriverBtn = view.findViewById(R.id.registerDriverBtn);
    }

    private void setupListeners() {
        registerDriverBtn.setOnClickListener(v -> navigateToDriverRegistration());
    }

    private void navigateToDriverRegistration() {
        Navigation.findNavController(requireView()).navigate(R.id.action_manageUsers_to_driverRegistration);
    }
}