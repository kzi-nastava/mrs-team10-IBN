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
import com.example.ubercorp.dto.RideDTO;

import java.util.List;

public class RideComplaintsAdapter extends ArrayAdapter<GetComplaintDTO> {

    private List<GetComplaintDTO> complaints;

    public RideComplaintsAdapter(@NonNull Context context, @NonNull List<GetComplaintDTO> objects) {
        super(context, R.layout.ride_complaint_item, objects);
        this.complaints = objects;
    }

    @Nullable
    @Override
    public GetComplaintDTO getItem(int position) {
        return complaints.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        GetComplaintDTO complaint = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ride_complaint_item,
                    parent, false);
        }
        TextView title = convertView.findViewById(R.id.title);
        TextView content = convertView.findViewById(R.id.content);
        title.setText(complaint.getUsername());
        content.setText(complaint.getContent());
        return convertView;
    }
}
