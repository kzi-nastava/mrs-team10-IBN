package com.example.ubercorp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ubercorp.R;
import com.example.ubercorp.dto.GetComplaintDTO;
import com.example.ubercorp.dto.GetReviewDTO;

import java.util.List;

public class RideReviewsAdapter extends ArrayAdapter<GetReviewDTO> {

    private List<GetReviewDTO> reviews;

    public RideReviewsAdapter(@NonNull Context context, @NonNull List<GetReviewDTO> objects) {
        super(context, R.layout.ride_review_item, objects);
        this.reviews = objects;
    }

    @Nullable
    @Override
    public GetReviewDTO getItem(int position) {
        return reviews.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        GetReviewDTO review = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ride_complaint_item,
                    parent, false);
        }
        TextView title = convertView.findViewById(R.id.title);
        TextView content = convertView.findViewById(R.id.content);
        TextView driverRating = convertView.findViewById(R.id.driver_rating);
        TextView vehicleRating = convertView.findViewById(R.id.vehicle_rating);
        title.setText(review.getUsername());
        content.setText(review.getComment());
        driverRating.setText(driverRating.getText() + " " + Double.toString(review.getDriverRating()));
        vehicleRating.setText(vehicleRating.getText() + " " + Double.toString(review.getVehicleRating()));
        return convertView;
    }
}

