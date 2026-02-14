package com.example.ubercorp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ubercorp.R;
import com.example.ubercorp.dto.ChatMessageDTO;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private final List<ChatMessageDTO> messages = new ArrayList<>();
    private final String currentUserEmail;

    public ChatAdapter(String email) {
        this.currentUserEmail = email;
    }

    public void setMessages(List<ChatMessageDTO> msgs) {
        messages.clear();
        if (msgs != null) messages.addAll(msgs);
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessageDTO msg) {
        messages.add(msg);
        notifyItemInserted(messages.size() - 1);
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
