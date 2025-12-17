package com.example.ubercorp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.ubercorp.R;
import com.example.ubercorp.databinding.FragmentRideHistoryBinding;
import com.example.ubercorp.model.Ride;

import java.util.ArrayList;
import java.util.Date;

public class RideHistoryFragment extends Fragment {

    public static ArrayList<Ride> rides = new ArrayList<Ride>();
    private RideHistoryViewModel ridesViewModel;
    private FragmentRideHistoryBinding binding;

    private boolean isFirstSelection;

    public RideHistoryFragment() {
    }
    public static RideHistoryFragment newInstance() {
        return new RideHistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ridesViewModel = new ViewModelProvider(this).get(RideHistoryViewModel.class);

        binding = FragmentRideHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        prepareRidesList(rides);

        FragmentTransition.to(RideHistoryListFragment.newInstance(rides), getActivity(), false,
                R.id.scroll_rides_list);

        return root;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }

    private void prepareRidesList(ArrayList<Ride> rides){
        rides.clear();
        rides.add(new Ride(1L, "Alekse Santica 5, Novi Sad", "Mileve Maric 40, Novi Sad", new Date(12,12,2025), new Date(12,12,2025), 824.00));
        rides.add(new Ride(2L, "Alekse Santica 4, Novi Sad", "Mileve Maric 40, Novi Sad", new Date(12,12,2025), new Date(12,12,2025), 824.00));
        rides.add(new Ride(3L, "Alekse Santica 3, Novi Sad", "Mileve Maric 40, Novi Sad", new Date(12,12,2025), new Date(12,12,2025), 824.00));
        rides.add(new Ride(4L, "Alekse Santica 2, Novi Sad", "Mileve Maric 40, Novi Sad", new Date(12,12,2025), new Date(12,12,2025), 824.00));
        rides.add(new Ride(5L, "Alekse Santica 1, Novi Sad", "Mileve Maric 40, Novi Sad", new Date(12,12,2025), new Date(12,12,2025), 824.00));
        rides.add(new Ride(6L, "Alekse Santica 5, Novi Sad", "Mileve Maric 40, Novi Sad", new Date(12,12,2025), new Date(12,12,2025), 824.00));
        rides.add(new Ride(7L, "Alekse Santica 5, Novi Sad", "Mileve Maric 40, Novi Sad", new Date(12,12,2025), new Date(12,12,2025), 824.00));
        rides.add(new Ride(8L, "Alekse Santica 5, Novi Sad", "Mileve Maric 40, Novi Sad", new Date(12,12,2025), new Date(12,12,2025), 824.00));
        rides.add(new Ride(9L, "Alekse Santica 5, Novi Sad", "Mileve Maric 40, Novi Sad", new Date(12,12,2025), new Date(12,12,2025), 824.00));
    }
}