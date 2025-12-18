package com.example.ubercorp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ubercorp.R;
import com.example.ubercorp.interfaces.onRideClickListener;
import com.example.ubercorp.model.Ride;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RideHistoryListAdapter extends ArrayAdapter<Ride> {

    private ArrayList<Ride> aRides;
    private onRideClickListener listener;
    public RideHistoryListAdapter(@NonNull Context context, ArrayList<Ride> rides, onRideClickListener listener) {
        super(context, R.layout.ride_card_item, rides);
        aRides = rides;
        this.listener = listener;
    }


    @Override
    public int getCount() {
        return aRides.size();
    }


    @Nullable
    @Override
    public Ride getItem(int position) {
        return aRides.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Ride ride = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ride_card_item,
                    parent, false);
        }
        ConstraintLayout rideCard = convertView.findViewById(R.id.ride_card_item);
        TextView startLocation = convertView.findViewById(R.id.startInput);
        TextView destination = convertView.findViewById(R.id.endInput);
        TextView start = convertView.findViewById(R.id.startTime);
        TextView end = convertView.findViewById(R.id.endTime);
        TextView price = convertView.findViewById(R.id.price);

        if(ride != null) {
            startLocation.setText(ride.getStartLocation());
            destination.setText(ride.getDestination());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            start.setText(sdf.format(ride.getStart()));
            end.setText(sdf.format(ride.getEnd()));
            price.setText(ride.getPrice().toString());
            // Handle click on the item at 'position'
            rideCard.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRideClick(ride);
                }
                Log.i("UberComp", "Clicked: " + ride.getStartLocation() + ", id: " +
                        ride.getId().toString());
                Toast.makeText(getContext(), "Clicked: " + ride.getStartLocation() +
                        ", id: " + ride.getId().toString(), Toast.LENGTH_SHORT).show();
            });

        }

        return convertView;
    }
}

