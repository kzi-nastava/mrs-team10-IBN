package com.example.ubercorp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubercorp.databinding.CurrentRideBinding;
import com.example.ubercorp.dto.DriversRidesDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CurrentRidesAdapter extends RecyclerView.Adapter<CurrentRidesAdapter.CurrentRideViewHolder> {

    private List<DriversRidesDTO> rides;

    public CurrentRidesAdapter(List<DriversRidesDTO> rides) {
        this.rides = rides;
    }

    @NonNull
    @Override
    public CurrentRideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CurrentRideBinding binding = CurrentRideBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CurrentRideViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentRideViewHolder holder, int position) {
        holder.bind(rides.get(position));
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    class CurrentRideViewHolder extends RecyclerView.ViewHolder {

        private CurrentRideBinding binding;

        public CurrentRideViewHolder(@NonNull CurrentRideBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DriversRidesDTO ride) {
            binding.nameTextView.setText(ride.getName() + " " + ride.getLastname());
            binding.emailTextView.setText(ride.getEmail());
            binding.startInput.setText(ride.getStartLocation());
            binding.endInput.setText(ride.getEndLocation());

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            if (ride.getStartTime() != null)
                binding.startTime.setText(ride.getStartTime().format(timeFormatter));

            if (ride.getEndTime() != null)
                binding.endTime.setText(ride.getEndTime().format(timeFormatter));

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            binding.startDate.setText(ride.getStartTime().format(dateFormatter));
        }
    }
}
