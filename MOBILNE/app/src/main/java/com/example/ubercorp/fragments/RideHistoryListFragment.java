package com.example.ubercorp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ubercorp.R;
import com.example.ubercorp.databinding.FragmentRideHistoryBinding;
import com.example.ubercorp.databinding.FragmentRideHistoryListBinding;
import com.example.ubercorp.fragments.placeholder.PlaceholderContent;
import com.example.ubercorp.model.Ride;
import com.example.ubercorp.adapters.RideHistoryListAdapter;

import java.util.ArrayList;


public class RideHistoryListFragment extends ListFragment {

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
            adapter = new RideHistoryListAdapter(getActivity(), mRides);
            setListAdapter(adapter);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentRideHistoryListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}