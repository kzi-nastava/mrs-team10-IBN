package com.example.ubercorp.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ubercorp.R;

public class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        public ChatViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.messageText);
        }
    }

