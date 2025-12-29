package com.example.ubercorp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.ubercorp.R;
import com.example.ubercorp.databinding.FragmentRideHistoryListBinding;
import com.example.ubercorp.interfaces.onRideClickListener;
import com.example.ubercorp.model.Ride;
import com.example.ubercorp.adapters.RideHistoryListAdapter;
import java.util.ArrayList;


public class RideHistoryListFragment extends ListFragment implements onRideClickListener {

    private RideHistoryListAdapter adapter;
    private static final String ARG_PARAM = "param";
    private ArrayList<Ride> mRides;
    private FragmentRideHistoryListBinding binding;


    public RideHistoryListFragment() {
    }

    @SuppressWarnings("unused")
    public static RideHistoryListFragment newInstance(ArrayList<Ride> rides) {
        RideHistoryListFragment fragment = new RideHistoryListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM, rides);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mRides = getArguments().getParcelableArrayList(ARG_PARAM);
            adapter = new RideHistoryListAdapter(getActivity(), mRides, this);
            setListAdapter(adapter);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentRideHistoryListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRideClick(Ride ride) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("ride",ride);

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_history_to_details, bundle);
    }
}