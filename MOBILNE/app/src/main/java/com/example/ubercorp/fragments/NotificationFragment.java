package com.example.ubercorp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ubercorp.R;
import com.example.ubercorp.adapters.NotificationAdapter;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.NotificationService;
import com.example.ubercorp.dto.AppNotificationDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<AppNotificationDTO> notifications = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        loadNotifications();

        return view;
    }

    private void loadNotifications() {
        NotificationService service = ApiClient.getInstance().createService(NotificationService.class);
        String token = getToken();

        Call<List<AppNotificationDTO>> call = service.getNotifications("Bearer " + token);
        call.enqueue(new Callback<List<AppNotificationDTO>>() {
            @Override
            public void onResponse(Call<List<AppNotificationDTO>> call, Response<List<AppNotificationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notifications.clear();
                    notifications.addAll(response.body());

                    Collections.sort(notifications, (n1, n2) -> Long.compare(n2.getId(), n1.getId()));

                    adapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onFailure(Call<List<AppNotificationDTO>> call, Throwable t) {
                // Handle error
            }
        });
    }

    private String getToken() {
        return getActivity().getSharedPreferences("uber_corp", 0)
                .getString("auth_token", null);
    }
}