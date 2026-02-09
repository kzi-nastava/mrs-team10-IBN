package com.example.ubercorp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ubercorp.databinding.DialogBlockUserBinding;
import com.example.ubercorp.dto.UserDTO;

public class BlockUserDialog extends DialogFragment {

    private DialogBlockUserBinding binding;
    private UserDTO user;
    private boolean isDriver;
    private OnBlockListener listener;

    public interface OnBlockListener {
        void onBlock(String reason);
    }

    public BlockUserDialog(UserDTO user, boolean isDriver, OnBlockListener listener) {
        this.user = user;
        this.isDriver = isDriver;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogBlockUserBinding.inflate(LayoutInflater.from(getContext()));

        binding.userNameTextView.setText(String.format("Block %s: %s %s",
                isDriver ? "Driver" : "Passenger",
                user.getName(),
                user.getLastname()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot())
                .setPositiveButton("Block", (dialog, which) -> {
                    String reason = binding.reasonEditText.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a reason", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (listener != null) {
                        listener.onBlock(reason);
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