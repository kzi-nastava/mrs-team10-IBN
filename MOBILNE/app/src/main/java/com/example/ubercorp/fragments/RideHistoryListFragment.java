package com.example.ubercorp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.ubercorp.R;
import com.example.ubercorp.databinding.FragmentRideHistoryListBinding;
import com.example.ubercorp.dto.GetRideDTO;
import com.example.ubercorp.dto.RideDTO;
import com.example.ubercorp.interfaces.onRideClickListener;
import com.example.ubercorp.managers.RideManager;
import com.example.ubercorp.adapters.RideHistoryListAdapter;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RideHistoryListFragment extends ListFragment implements onRideClickListener {

    private RideHistoryListAdapter adapter;
    private FragmentRideHistoryListBinding binding;

    private ArrayList<RideDTO> mRides = new ArrayList<>();
    private RideManager rideManager;

    private int currentPage = 0;
    private boolean isLastPage = false;



    public RideHistoryListFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new RideHistoryListAdapter(getActivity(), mRides, this);
        setListAdapter(adapter);

        rideManager = new RideManager(requireContext());

        loadNextPage();
    }

    private void loadNextPage() {
        if (isLastPage) return;

        rideManager.loadDriverRides(currentPage, 10, new Callback<>() {
            @Override
            public void onResponse(Call<GetRideDTO> call, Response<GetRideDTO> response) {
                if (response.isSuccessful() && response.body() != null) {

                    GetRideDTO dto = response.body();

                    for (RideDTO r : dto.getContent()) {
                        mRides.add(r);
                    }

                    adapter.notifyDataSetChanged();

                    currentPage++;
                    isLastPage = currentPage >= dto.getTotalPages();
                }
            }

            @Override
            public void onFailure(Call<GetRideDTO> call, Throwable t) {
                Log.e("RideHistory", t.getMessage());
            }
        });
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
    public void onRideClick(RideDTO ride) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("ride",ride);

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_history_to_details, bundle);
    }
}