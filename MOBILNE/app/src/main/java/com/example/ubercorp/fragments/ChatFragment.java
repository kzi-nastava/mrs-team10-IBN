package com.example.ubercorp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.ubercorp.dto.ChatRoomDTO;
import com.example.ubercorp.utils.JwtUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ChatFragment extends Fragment {
    private ChatFragmentBinding binding;
    private ChatAdapter adapter;
    private MyNotificationManager ws;
    private ChatService chatApi;
    private String currentUserEmail;
    private String receiver;
    private Long chatRoomId;
    private String authToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ChatFragmentBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            chatRoomId = getArguments().getLong("room_id");
            receiver = getArguments().getString("receiver");
        }

        SharedPreferences sharedPref = getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", null);
        currentUserEmail = JwtUtils.getEmailFromToken(authToken);

        adapter = new ChatAdapter(currentUserEmail);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(lm);
        binding.recyclerView.setAdapter(adapter);

        chatApi = ApiClient.getInstance().createService(ChatService.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                ws = MyNotificationManager.getInstance(getContext());
                ws.connect(BuildConfig.API_HOST + "socket", authToken, currentUserEmail);
                requireActivity().runOnUiThread(() -> {
                    String role = JwtUtils.getRoleFromToken(authToken);
                    loadChatRoom(role);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.sendBtn.setOnClickListener(v -> sendMessage());
    }

    private void loadChatRoom(String role) {
        if (!role.equals("administrator")) {
            chatApi.getMyChat("Bearer " + authToken).enqueue(new Callback<ChatRoomDTO>() {
                @Override
                public void onResponse(Call<ChatRoomDTO> call, Response<ChatRoomDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        setupChatRoom(response.body());
                    }

                }
                @Override
                public void onFailure(Call<ChatRoomDTO> call, Throwable t) {}
            });
        } else {
            chatApi.getChatRoom("Bearer " + authToken, chatRoomId).enqueue(new Callback<ChatRoomDTO>() {
                @Override
                public void onResponse(Call<ChatRoomDTO> call, Response<ChatRoomDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        setupChatRoom(response.body());
                    }
                }
                @Override
                public void onFailure(Call<ChatRoomDTO> call, Throwable t) {}
            });
        }
    }
    private void setupChatRoom(ChatRoomDTO room) {
        chatRoomId = room.getId();
        currentUserEmail = room.getCurrentUserEmail();

        adapter.setMessages(room.getMessages());
        binding.recyclerView.scrollToPosition(Math.max(0, adapter.getItemCount() - 1));

        if (ws != null) {
            String role = JwtUtils.getRoleFromToken(authToken);

            if (role.equals("administrator")) {
                subscribeToAdminTopic();
            } else {
                subscribeToTopicByEmail(currentUserEmail);
            }
        }
    }

    private void subscribeToTopicByEmail(String userEmail) {
        ws.subscribeToTopicByEmail(userEmail, msg -> {
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                adapter.addMessage(msg);
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            });
        });
    }

    private void subscribeToAdminTopic() {
        ws.subscribeToAdminChat(msg -> {
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                adapter.addMessage(msg);
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            });
        });
    }


    private void sendMessage() {
        String text = binding.messageInput.getText().toString().trim();
        if (text.isEmpty() || ws == null || chatRoomId == null) return;

        ChatMessageDTO msg = new ChatMessageDTO();
        msg.setContent(text);
        msg.setChatRoom(chatRoomId);
        msg.setEmail(currentUserEmail);

        ws.sendMessage(msg);
        requireActivity().runOnUiThread(() -> {
            adapter.addMessage(msg);
            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            binding.messageInput.setText("");
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ws != null) ws.disconnect();
    }
}
