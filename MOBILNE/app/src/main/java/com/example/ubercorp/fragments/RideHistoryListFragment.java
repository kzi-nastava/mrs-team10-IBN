package com.example.ubercorp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.ListFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

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
    private String startFrom;
    private String startTo;
    private String sort;

    private boolean isLoading = false;


    public RideHistoryListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            startFrom = getArguments().getString("startDate");
            startTo = getArguments().getString("endDate");
            sort = getArguments().getString("sort");
        }

        getParentFragmentManager().setFragmentResultListener("query", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                if (bundle.getString("startDate") != null) startFrom = bundle.getString("startDate");
                if (bundle.getString("endDate") != null) startTo = bundle.getString("endDate");
                if (bundle.getString("sort") != null) sort = bundle.getString("sort");
                mRides.clear();
                currentPage = 0;
                isLastPage = false;
                loadNextPage();
            }
        });

        adapter = new RideHistoryListAdapter(getActivity(), mRides, this);
        setListAdapter(adapter);
        rideManager = new RideManager(requireContext());
        loadNextPage();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (!isLastPage && totalItemCount > 0 &&
                        (firstVisibleItem + visibleItemCount >= totalItemCount)) {
                    loadNextPage();
                }
            }
        });
    }


    private void loadNextPage() {
        if (isLastPage || isLoading) return;

        isLoading = true;

        Log.d("RideHistoryList", "Loading page: " + currentPage +
                ", From: " + startFrom + ", To: " + startTo);

        rideManager.loadRideHistory(currentPage, 2, startFrom, startTo, sort, new Callback<>() {
            @Override
            public void onResponse(Call<GetRideDTO> call, Response<GetRideDTO> response) {
                isLoading = false;
                Log.d("RideHistoryList", "Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    GetRideDTO dto = response.body();
                    Log.d("RideHistoryList", "Rides count: " + dto.getContent().size());
                    for (RideDTO r : dto.getContent()) {
                        mRides.add(r);
                    }
                    adapter.notifyDataSetChanged();

                    currentPage++;
                    isLastPage = currentPage >= dto.getTotalPages();
                } else {
                    Log.e("RideHistoryList", "Response not successful");
                }
            }
            @Override
            public void onFailure(Call<GetRideDTO> call, Throwable t) {
                isLoading = false;
                Log.e("RideHistoryList", "Error: " + t.getMessage());
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