package com.example.ubercorp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ubercorp.BuildConfig;
import com.example.ubercorp.adapters.ChatAdapter;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.ChatService;
import com.example.ubercorp.databinding.ChatFragmentBinding;
import com.example.ubercorp.managers.MyNotificationManager;
import com.example.ubercorp.dto.ChatMessageDTO;
import com.example.ubercorp.model.ChatRoom;
import com.example.ubercorp.utils.JwtUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {
    private ChatFragmentBinding binding;
    private ChatAdapter adapter;
    private List<ChatMessageDTO> messages = new ArrayList<>();
    private MyNotificationManager ws;
    private ChatService chatApi;
    private String currentUserEmail;
    private Long chatRoomId;
    private String authToken;
    private ExecutorService executorService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ChatFragmentBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            chatRoomId = getArguments().getLong("CHAT_ROOM_ID");
        }

        SharedPreferences sharedPref = getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", null);
        String email = JwtUtils.getEmailFromToken(authToken);

        currentUserEmail = email;
        adapter = new ChatAdapter(messages, email);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatApi = ApiClient.getInstance().createService(ChatService.class);
        executorService = Executors.newFixedThreadPool(2);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        executorService.execute(() -> {
            try {
                ws = MyNotificationManager.getInstance(getContext());
                ws.connect(BuildConfig.API_HOST + "socket", authToken, currentUserEmail);
            } catch (Exception e) {
            }
        });

        executorService.execute(() -> {
            try {
                loadChatRoom(authToken);
            } catch (Exception e) {
            }
        });

        binding.sendBtn.setOnClickListener(v -> sendMessage());
    }

    private void loadChatRoom(String token) {
        chatApi.getMyChat("Bearer " + token).enqueue(new Callback<ChatRoom>() {
            @Override
            public void onResponse(Call<ChatRoom> call, Response<ChatRoom> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatRoom room = response.body();
                    chatRoomId = room.getId();

                    if (room.getMessages() != null) {
                        messages.addAll(room.getMessages());
                    }

                    currentUserEmail = room.getCurrentUserEmail();
                    requireActivity().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        binding.recyclerView.scrollToPosition(Math.max(0, messages.size() - 1));
                    });

                    if (ws != null && room.getId() != null) {
                        ws.subscribeToChat(room.getId(), msg -> {
                            if (requireActivity() != null) {
                                requireActivity().runOnUiThread(() -> {
                                    messages.add(msg);
                                    adapter.notifyItemInserted(messages.size() - 1);
                                    binding.recyclerView.scrollToPosition(messages.size() - 1);
                                });
                            }
                        });
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatRoom> call, Throwable t) {
            }
        });
    }

    private void sendMessage() {
        String text = binding.messageInput.getText().toString();

        if (text.trim().isEmpty()) {
            return;
        }

        ChatMessageDTO msg = new ChatMessageDTO();
        msg.setContent(text);
        msg.setChatRoom(chatRoomId);
        msg.setEmail(currentUserEmail);
        msg.setChatRoom(chatRoomId);


        if (ws != null) {
            ws.sendMessage(msg);

            messages.add(msg);
            adapter.notifyItemInserted(messages.size() - 1);
            binding.messageInput.setText("");
            binding.recyclerView.scrollToPosition(messages.size() - 1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ws != null) {
            ws.disconnect();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}