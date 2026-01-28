package com.example.ubercorp.adapters;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.ubercorp.R;
import com.example.ubercorp.dto.CreatedUserDTO;
import com.example.ubercorp.dto.RideDTO;
import com.example.ubercorp.interfaces.onRideClickListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.io.File;
import java.util.ArrayList;

public class RideHistoryListAdapter extends ArrayAdapter<RideDTO> {

    private ArrayList<RideDTO> aRides;
    private onRideClickListener listener;
    public RideHistoryListAdapter(@NonNull Context context, ArrayList<RideDTO> rides, onRideClickListener listener) {
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
    public RideDTO getItem(int position) {
        return aRides.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RideDTO ride = getItem(position);
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
        TextView date = convertView.findViewById(R.id.startDate);

        if(ride != null) {
            startLocation.setText(ride.getStartLocation());
            destination.setText(ride.getDestination());

            start.setText(ride.getStartTime().substring(11,16));

            end.setText(ride.getEndTime().substring(11,16));
            price.setText(ride.getPrice().toString() + " RSD");
            date.setText(ride.getStartTime().substring(0,10));


            LinearLayout avatarsLayout = convertView.findViewById(R.id.avatars);

            avatarsLayout.removeAllViews();

            for (CreatedUserDTO user : ride.getPassengers()) {

                ShapeableImageView avatar = new ShapeableImageView(convertView.getContext());
                int size = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 36,
                        convertView.getResources().getDisplayMetrics()
                );
                int overlap = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 10,
                        convertView.getResources().getDisplayMetrics()
                );

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(size, size);

                if (avatarsLayout.getChildCount() > 0)
                    params.setMarginStart(-overlap);


                avatar.setLayoutParams(params);
                avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
                avatar.setPadding(0, 0, 0, 0);

                avatar.setShapeAppearanceModel(
                        ShapeAppearanceModel.builder()
                                .setAllCorners(
                                        com.google.android.material.shape.CornerFamily.ROUNDED,
                                        size / 2f
                                )
                                .build()
                );
                avatar.setClipToOutline(true);
                loadAvatar(avatar, user.getImage());
                avatarsLayout.addView(avatar);
            }


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

    private void loadAvatar(ShapeableImageView imageView, String path) {

        if (path == null || path.isEmpty()) {
            imageView.setImageResource(R.drawable.placeholderpfp);
            return;
        }

        File file = new File(path);

        if (!file.exists()) {
            imageView.setImageResource(R.drawable.placeholderpfp);
            return;
        }

        Glide.with(imageView.getContext())
                .load(file)
                .placeholder(R.drawable.placeholderpfp)
                .error(R.drawable.placeholderpfp)
                .into(imageView);
    }

}

