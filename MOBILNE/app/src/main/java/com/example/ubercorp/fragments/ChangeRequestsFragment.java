package com.example.ubercorp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubercorp.R;
import com.example.ubercorp.adapters.ChangeRequestsAdapter;
import com.example.ubercorp.dto.DriverChangeRequestDTO;
import com.example.ubercorp.managers.AdminManager;

import java.util.List;

public class ChangeRequestsFragment extends Fragment implements
        AdminManager.AdminActionsListener,
        ChangeRequestsAdapter.OnRequestActionListener {

    private RecyclerView recyclerViewRequests;
    private ChangeRequestsAdapter adapter;
    private AdminManager adminManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_requests, container, false);

        adminManager = new AdminManager(getContext(), this);

        recyclerViewRequests = view.findViewById(R.id.recyclerViewRequests);
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(getContext()));

        loadChangeRequests();

        return view;
    }

    private void loadChangeRequests() {
        adminManager.loadChangeRequests();
    }

    @Override
    public void onChangeRequestsLoaded(List<DriverChangeRequestDTO> requests) {
        adapter = new ChangeRequestsAdapter(requests, this);
        recyclerViewRequests.setAdapter(adapter);
    }

    @Override
    public void onActionSuccess(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        loadChangeRequests();
    }

    @Override
    public void onActionFailed(String error) {
        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onApproveRequest(Long requestId) {
        adminManager.approveChangeRequest(requestId);
    }

    @Override
    public void onRejectRequest(Long requestId) {
        adminManager.rejectChangeRequest(requestId);
    }
}