package com.example.ubercorp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubercorp.databinding.ItemUserBinding;
import com.example.ubercorp.dto.UserDTO;

import java.util.List;

public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.DriverViewHolder> {

    private List<UserDTO> drivers;
    private OnDriverActionListener listener;

    public interface OnDriverActionListener {
        void onBlock(UserDTO driver);
        void onUnblock(UserDTO driver);
    }

    public DriversAdapter(List<UserDTO> drivers, OnDriverActionListener listener) {
        this.drivers = drivers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DriverViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {
        holder.bind(drivers.get(position));
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    class DriverViewHolder extends RecyclerView.ViewHolder {
        private ItemUserBinding binding;

        public DriverViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(UserDTO driver) {
            binding.nameTextView.setText(driver.getName());
            binding.lastnameTextView.setText(driver.getLastname());
            binding.emailTextView.setText(driver.getEmail());
            binding.phoneTextView.setText(driver.getPhoneNumber());
            binding.statusTextView.setText(driver.getAccountStatus());

            if ("BLOCKED".equals(driver.getAccountStatus())) {
                binding.statusTextView.setTextColor(Color.RED);
                binding.actionButton.setText("Unblock");
                binding.actionButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                binding.actionButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onUnblock(driver);
                    }
                });
            } else {
                binding.statusTextView.setTextColor(Color.parseColor("#4CAF50"));
                binding.actionButton.setText("Block");
                binding.actionButton.setBackgroundColor(Color.parseColor("#F44336"));
                binding.actionButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onBlock(driver);
                    }
                });
            }
        }
    }
}