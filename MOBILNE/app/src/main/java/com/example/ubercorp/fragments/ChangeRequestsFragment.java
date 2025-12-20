package com.example.ubercorp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubercorp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeRequestsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RequestsAdapter adapter;
    private List<ChangeRequest> requests = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadSampleData();

        adapter = new RequestsAdapter(requests, this::showRequestDetails);
        recyclerView.setAdapter(adapter);
    }

    private void loadSampleData() {
        // Profile change request
        Map<String, String> oldData1 = new HashMap<>();
        oldData1.put("Name", "John Doe");
        oldData1.put("Email", "john.old@email.com");
        oldData1.put("Phone", "+123456789");

        Map<String, String> newData1 = new HashMap<>();
        newData1.put("Name", "John Doe");
        newData1.put("Email", "john.new@email.com");
        newData1.put("Phone", "+123456789");

        requests.add(new ChangeRequest(
                "1",
                "profile",
                "John Doe",
                "2024-12-15",
                "pending",
                new Changes(oldData1, newData1)
        ));

        // Vehicle change request
        Map<String, String> oldData2 = new HashMap<>();
        oldData2.put("Vehicle Type", "Standard");
        oldData2.put("Number of Seats", "4");
        oldData2.put("Baby Transport", "No");
        oldData2.put("Pet Transport", "No");

        Map<String, String> newData2 = new HashMap<>();
        newData2.put("Vehicle Type", "Luxury");
        newData2.put("Number of Seats", "5");
        newData2.put("Baby Transport", "Yes");
        newData2.put("Pet Transport", "Yes");

        requests.add(new ChangeRequest(
                "2",
                "vehicle",
                "Jane Smith",
                "2024-12-18",
                "pending",
                new Changes(oldData2, newData2)
        ));
    }

    private void showRequestDetails(ChangeRequest request) {
        View dialogView = getLayoutInflater().inflate(R.layout.change_request_details, null);

        TextView subtitleText = dialogView.findViewById(R.id.tvDialogSubtitle);
        LinearLayout changesContainer = dialogView.findViewById(R.id.llChangesContainer);
        MaterialButton btnClose = dialogView.findViewById(R.id.btnClose);
        MaterialButton btnReject = dialogView.findViewById(R.id.btnReject);
        MaterialButton btnAccept = dialogView.findViewById(R.id.btnAccept);

        subtitleText.setText(request.getDriverName());

        // Populate changes
        for (String key : request.getChanges().getOldData().keySet()) {
            View changeItemView = getLayoutInflater().inflate(R.layout.item_change, changesContainer, false);

            TextView labelText = changeItemView.findViewById(R.id.tvChangeLabel);
            TextView oldValueText = changeItemView.findViewById(R.id.tvOldValue);
            TextView newValueText = changeItemView.findViewById(R.id.tvNewValue);

            labelText.setText(key + ":");
            oldValueText.setText(request.getChanges().getOldData().get(key));
            newValueText.setText(request.getChanges().getNewData().get(key));

            changesContainer.addView(changeItemView);
        }

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView);

        AlertDialog alertDialog = dialog.create();

        btnClose.setOnClickListener(v -> alertDialog.dismiss());

        if (request.getStatus().equals("pending")) {
            btnReject.setVisibility(View.VISIBLE);
            btnAccept.setVisibility(View.VISIBLE);

            btnReject.setOnClickListener(v -> {
                handleReject(request.getId());
                alertDialog.dismiss();
            });

            btnAccept.setOnClickListener(v -> {
                handleApprove(request.getId());
                alertDialog.dismiss();
            });
        } else {
            btnReject.setVisibility(View.GONE);
            btnAccept.setVisibility(View.GONE);
        }

        alertDialog.show();
    }

    private void handleReject(String requestId) {
        for (ChangeRequest request : requests) {
            if (request.getId().equals(requestId)) {
                request.setStatus("rejected");
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void handleApprove(String requestId) {
        for (ChangeRequest request : requests) {
            if (request.getId().equals(requestId)) {
                request.setStatus("approved");
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    // Data Classes
    public static class ChangeRequest {
        private String id;
        private String type;
        private String driverName;
        private String requestDate;
        String status;
        private Changes changes;

        public ChangeRequest(String id, String type, String driverName, String requestDate,
                             String status, Changes changes) {
            this.id = id;
            this.type = type;
            this.driverName = driverName;
            this.requestDate = requestDate;
            this.status = status;
            this.changes = changes;
        }

        public String getId() { return id; }
        public String getType() { return type; }
        public String getDriverName() { return driverName; }
        public String getRequestDate() { return requestDate; }
        public String getStatus() { return status; }
        public Changes getChanges() { return changes; }
        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class Changes {
        private Map<String, String> oldData;
        private Map<String, String> newData;

        public Changes(Map<String, String> oldData, Map<String, String> newData) {
            this.oldData = oldData;
            this.newData = newData;
        }

        public Map<String, String> getOldData() { return oldData; }
        public Map<String, String> getNewData() { return newData; }
    }

    // RecyclerView Adapter
    public static class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

        private List<ChangeRequest> requests;
        private OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(ChangeRequest request);
        }

        public RequestsAdapter(List<ChangeRequest> requests, OnItemClickListener listener) {
            this.requests = requests;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.admin_table_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChangeRequest request = requests.get(position);

            holder.typeIcon.setText(request.getType().equals("profile") ? "👤" : "🚗");
            holder.driverName.setText(request.getDriverName());
            holder.requestDate.setText("🕐 " + request.getRequestDate());

            holder.btnReview.setOnClickListener(v -> listener.onItemClick(request));
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView typeIcon;
            TextView driverName;
            TextView requestDate;
            MaterialButton btnReview;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                typeIcon = itemView.findViewById(R.id.tvTypeIcon);
                driverName = itemView.findViewById(R.id.tvDriverName);
                requestDate = itemView.findViewById(R.id.tvRequestDate);
                btnReview = itemView.findViewById(R.id.btnReview);
            }
        }
    }
}