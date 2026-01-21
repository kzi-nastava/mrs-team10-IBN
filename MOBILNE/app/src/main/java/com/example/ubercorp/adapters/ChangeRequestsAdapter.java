package com.example.ubercorp.adapters;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubercorp.R;
import com.example.ubercorp.dto.DriverChangeRequestDTO;
import com.example.ubercorp.utils.ImageHelper;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ChangeRequestsAdapter extends RecyclerView.Adapter<ChangeRequestsAdapter.ViewHolder> {

    private List<DriverChangeRequestDTO> requests;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onApproveRequest(Long requestId);
        void onRejectRequest(Long requestId);
    }

    public ChangeRequestsAdapter(List<DriverChangeRequestDTO> requests, OnRequestActionListener listener) {
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
        DriverChangeRequestDTO request = requests.get(position);
        holder.bind(request, listener);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDriverName, tvDate;
        MaterialButton btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvTypeIcon);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            tvDate = itemView.findViewById(R.id.tvRequestDate);
            btnView = itemView.findViewById(R.id.btnReview);
        }

        public void bind(DriverChangeRequestDTO request, OnRequestActionListener listener) {
            String typeEmoji;
            if ("profile".equalsIgnoreCase(request.getType())) {
                typeEmoji = "\uD83D\uDC64";
            } else if ("vehicle".equalsIgnoreCase(request.getType())) {
                typeEmoji = "\uD83D\uDE97";
            } else {
                typeEmoji = "";
            }
            tvType.setText(typeEmoji);

            tvDriverName.setText(request.getDriverName());

            String formattedDate = formatDate(request.getRequestDate());
            tvDate.setText(formattedDate);

            btnView.setOnClickListener(v -> showDetailsDialog(request, listener));
        }

        private String formatDate(String isoDate) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(isoDate);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
                return dateTime.format(formatter);
            } catch (Exception e) {
                e.printStackTrace();
                return isoDate;
            }
        }

        private void showDetailsDialog(DriverChangeRequestDTO request, OnRequestActionListener listener) {
            Dialog dialog = new Dialog(itemView.getContext());
            dialog.setContentView(R.layout.change_request_details);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            TextView tvDialogSubtitle = dialog.findViewById(R.id.tvDialogSubtitle);
            LinearLayout llChangesContainer = dialog.findViewById(R.id.llChangesContainer);
            MaterialButton btnClose = dialog.findViewById(R.id.btnClose);
            MaterialButton btnReject = dialog.findViewById(R.id.btnReject);
            MaterialButton btnAccept = dialog.findViewById(R.id.btnAccept);

            tvDialogSubtitle.setText(request.getDriverName() + " - " + request.getType());

            populateChanges(llChangesContainer, request);

            btnClose.setOnClickListener(v -> dialog.dismiss());

            btnReject.setOnClickListener(v -> {
                listener.onRejectRequest(request.getId());
                dialog.dismiss();
            });

            btnAccept.setOnClickListener(v -> {
                listener.onApproveRequest(request.getId());
                dialog.dismiss();
            });

            dialog.show();
        }

        private void populateChanges(LinearLayout container, DriverChangeRequestDTO request) {
            container.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(container.getContext());

            if (request.getOldImage() != null && request.getNewImage() != null &&
                    !request.getOldImage().isEmpty() && !request.getNewImage().isEmpty()) {
                View imageChangeView = inflater.inflate(R.layout.item_image_change, container, false);

                ImageView ivOldImage = imageChangeView.findViewById(R.id.ivOldImage);
                ImageView ivNewImage = imageChangeView.findViewById(R.id.ivNewImage);

                ImageHelper.setProfileImage(request.getOldImage(), ivOldImage);
                ImageHelper.setProfileImage(request.getNewImage(), ivNewImage);

                container.addView(imageChangeView);
            }

            if (request.getChanges() != null) {
                Map<String, String> oldData = request.getChanges().getOldData();
                Map<String, String> newData = request.getChanges().getNewData();

                if (oldData != null && newData != null) {
                    for (String key : newData.keySet()) {
                        String oldValue = oldData.get(key);
                        String newValue = newData.get(key);

                        if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
                            View changeView = inflater.inflate(R.layout.item_change, container, false);

                            TextView tvChangeLabel = changeView.findViewById(R.id.tvChangeLabel);
                            TextView tvOldValue = changeView.findViewById(R.id.tvOldValue);
                            TextView tvNewValue = changeView.findViewById(R.id.tvNewValue);

                            tvChangeLabel.setText(formatFieldName(key) + ":");
                            tvOldValue.setText(oldValue);
                            tvNewValue.setText(newValue);

                            container.addView(changeView);
                        }
                    }
                }
            }
        }

        private String formatFieldName(String fieldName) {
            String formatted = fieldName.replaceAll("([A-Z])", " $1")
                    .replaceAll("_", " ")
                    .trim();

            if (formatted.isEmpty()) return fieldName;

            return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
        }
    }
}