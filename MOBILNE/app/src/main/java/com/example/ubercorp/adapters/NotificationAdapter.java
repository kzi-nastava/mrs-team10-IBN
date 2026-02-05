package com.example.ubercorp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ubercorp.R;
import com.example.ubercorp.dto.AppNotificationDTO;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<AppNotificationDTO> notifications;

    public NotificationAdapter(List<AppNotificationDTO> notifications) {
        this.notifications = notifications;
        Collections.sort(this.notifications, (n1, n2) -> Long.compare(n2.getId(), n1.getId()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppNotificationDTO notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.content.setText(notification.getContent());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.notificationTitle);
            content = view.findViewById(R.id.notificationContent);
        }
    }
}