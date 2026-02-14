package com.example.ubercorp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.ubercorp.R;
import com.example.ubercorp.adapters.ChatInboxAdapter;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.ChatService;
import com.example.ubercorp.dto.ChatInboxDTO;
import com.example.ubercorp.managers.MyNotificationManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminChatFragment extends Fragment implements ChatInboxAdapter.OnChatItemClickListener {

    private ChatService chatService;
    private ChatInboxAdapter adapter;
    private RecyclerView inboxRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_chat, container, false);

        inboxRecycler = view.findViewById(R.id.inboxRecycler);
        chatService = ApiClient.getInstance().createService(ChatService.class);

        inboxRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatInboxAdapter(new ArrayList<>(), this);
        inboxRecycler.setAdapter(adapter);
        loadChatRooms();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyNotificationManager notificationManager = MyNotificationManager.getInstance(getContext());
        notificationManager.subscribeToAdminChat(message -> {
        });
    }


    public void loadChatRooms() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);

        chatService.getAllChats("Bearer " + token).enqueue(new Callback<List<ChatInboxDTO>>() {
            @Override
            public void onResponse(Call<List<ChatInboxDTO>> call, Response<List<ChatInboxDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateList(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ChatInboxDTO>> call, Throwable t) {
            }
        });
    }

    @Override
    public void onChatItemClick(ChatInboxDTO chatInbox) {
        Bundle bundle = new Bundle();
        bundle.putLong("room_id", chatInbox.getChatRoom());
        bundle.putString("receiver", chatInbox.getUser().getEmail());

        NavController navController = NavHostFragment.findNavController(AdminChatFragment.this);
        navController.navigate(R.id.chat_layout, bundle);
    }
}