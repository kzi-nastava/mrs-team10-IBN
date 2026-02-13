package com.example.ubercorp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ubercorp.R;
import com.example.ubercorp.dto.ChatMessageDTO;

import java.util.List;
public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private List<ChatMessageDTO> messages;
    private String currentUserEmail;

    public ChatAdapter(List<ChatMessageDTO> messages, String email) {
        this.messages = messages;
        this.currentUserEmail = email;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getEmail().equals(currentUserEmail) ? 1 : 0;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = viewType == 1 ? R.layout.right_message : R.layout.left_message;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        holder.text.setText(messages.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}

