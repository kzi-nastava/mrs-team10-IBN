package com.example.ubercorp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ride_review_item,
                    parent, false);
        }
        TextView title = convertView.findViewById(R.id.title);
        TextView content = convertView.findViewById(R.id.content);
        LinearLayout driverRating = convertView.findViewById(R.id.driver_rating);
        LinearLayout vehicleRating = convertView.findViewById(R.id.vehicle_rating);
        title.setText(review.getUsername());
        content.setText(review.getComment());
        displayStarRating(driverRating, review.getDriverRating());
        displayStarRating(vehicleRating, review.getVehicleRating());
        return convertView;
    }

    private void displayStarRating(LinearLayout container, double rating) {
        int childCount = container.getChildCount();
        if (childCount > 1) {
            container.removeViews(1, childCount - 1);
        }

        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView(getContext());

            if (i <= rating) {
                star.setImageResource(R.drawable.ic_star_filled);
            } else {
                star.setImageResource(R.drawable.ic_star_border);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(24),
                    dpToPx(24)
            );
            params.setMargins(dpToPx(2), 0, dpToPx(2), 0);
            star.setLayoutParams(params);

            container.addView(star);
        }
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}

