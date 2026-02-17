package com.example.ubercorp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ubercorp.databinding.DialogUnblockUserBinding;
import com.example.ubercorp.dto.UserDTO;

public class UnblockUserDialog extends DialogFragment {

    private DialogUnblockUserBinding binding;
    private UserDTO user;
    private boolean isDriver;
    private OnUnblockListener listener;

    public interface OnUnblockListener {
        void onUnblock();
    }

    public UnblockUserDialog(UserDTO user, boolean isDriver, OnUnblockListener listener) {
        this.user = user;
        this.isDriver = isDriver;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogUnblockUserBinding.inflate(LayoutInflater.from(getContext()));

        binding.userNameTextView.setText(String.format("Unblock %s: %s %s",
                isDriver ? "Driver" : "Passenger",
                user.getName(),
                user.getLastname()));

        if (user.getBlockingReason() != null && !user.getBlockingReason().isEmpty()) {
            binding.blockingReasonTextView.setText("Blocking reason: " + user.getBlockingReason());
        } else {
            binding.blockingReasonTextView.setText("Blocking reason: Not specified");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot())
                .setPositiveButton("Unblock", (dialog, which) -> {
                    if (listener != null) {
                        listener.onUnblock();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dismiss());

        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}