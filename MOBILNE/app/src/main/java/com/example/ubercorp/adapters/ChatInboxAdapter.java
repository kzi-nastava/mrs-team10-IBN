package com.example.ubercorp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubercorp.databinding.InboxItemBinding;
import com.example.ubercorp.dto.ChatInboxDTO;
import java.util.List;

public class ChatInboxAdapter extends RecyclerView.Adapter<ChatInboxAdapter.ChatInboxViewHolder> {

    private List<ChatInboxDTO> chatList;
    private OnChatItemClickListener listener;

    public interface OnChatItemClickListener {
        void onChatItemClick(ChatInboxDTO chatInbox);
    }

    public ChatInboxAdapter(List<ChatInboxDTO> chatList, OnChatItemClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatInboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        InboxItemBinding binding = InboxItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ChatInboxViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatInboxViewHolder holder, int position) {
        ChatInboxDTO chat = chatList.get(position);
        holder.bind(chat, listener);
    }

    @Override
    public int getItemCount() {
        return chatList != null ? chatList.size() : 0;
    }

    public void updateList(List<ChatInboxDTO> newList) {
        this.chatList = newList;
        notifyDataSetChanged();
    }

    static class ChatInboxViewHolder extends RecyclerView.ViewHolder {
        private InboxItemBinding binding;

        public ChatInboxViewHolder(InboxItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChatInboxDTO chat, OnChatItemClickListener listener) {
            binding.userName.setText(chat.getUser().getName());
            binding.userPhone.setText(chat.getUser().getPhoneNumber());

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChatItemClick(chat);
                }
            });
        }
    }
}