package com.example.ubercorp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubercorp.databinding.ItemUserBinding;
import com.example.ubercorp.dto.UserDTO;

import java.util.List;

public class PassengersAdapter extends RecyclerView.Adapter<PassengersAdapter.PassengerViewHolder> {

    private List<UserDTO> passengers;
    private OnPassengerActionListener listener;

    public interface OnPassengerActionListener {
        void onBlock(UserDTO passenger);
        void onUnblock(UserDTO passenger);
    }

    public PassengersAdapter(List<UserDTO> passengers, OnPassengerActionListener listener) {
        this.passengers = passengers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PassengerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PassengerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerViewHolder holder, int position) {
        holder.bind(passengers.get(position));
    }

    @Override
    public int getItemCount() {
        return passengers.size();
    }

    class PassengerViewHolder extends RecyclerView.ViewHolder {
        private ItemUserBinding binding;

        public PassengerViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(UserDTO passenger) {
            binding.nameTextView.setText(passenger.getName());
            binding.lastnameTextView.setText(passenger.getLastname());
            binding.emailTextView.setText(passenger.getEmail());
            binding.phoneTextView.setText(passenger.getPhoneNumber());
            binding.statusTextView.setText(passenger.getAccountStatus());

            if ("BLOCKED".equals(passenger.getAccountStatus())) {
                binding.statusTextView.setTextColor(Color.RED);
                binding.actionButton.setText("Unblock");
                binding.actionButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                binding.actionButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onUnblock(passenger);
                    }
                });
            } else {
                binding.statusTextView.setTextColor(Color.parseColor("#4CAF50"));
                binding.actionButton.setText("Block");
                binding.actionButton.setBackgroundColor(Color.parseColor("#F44336"));
                binding.actionButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onBlock(passenger);
                    }
                });
            }
        }
    }
}