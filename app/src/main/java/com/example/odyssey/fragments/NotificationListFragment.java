package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.example.odyssey.R;
import com.example.odyssey.adapters.NotificationSummaryAdapter;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.clients.StompClient;
import com.example.odyssey.model.notifications.Notification;
import com.example.odyssey.utils.TokenUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationListFragment extends Fragment {
    NotificationSummaryAdapter adapter;
    StompClient stompClient = null;
    ListView notifications;
    Boolean read;

    public NotificationListFragment() {
    }

    public static NotificationListFragment newInstance() {
        return new NotificationListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        read = null;
        if (stompClient != null) stompClient.disconnect();
        stompClient = new StompClient();
        stompClient.subscribe(() -> updateNotifications(read));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification_list, container, false);
        notifications = v.findViewById(R.id.notifications_list_view);

        adapter = new NotificationSummaryAdapter(requireContext(), v, new ArrayList<>());
        notifications.setAdapter(adapter);

        Button allBtn = v.findViewById(R.id.notification_all_toggle);
        Button readBtn = v.findViewById(R.id.notification_read_toggle);
        Button unreadBtn = v.findViewById(R.id.notification_unread_toggle);
        allBtn.setOnClickListener(btnView -> updateNotifications(null));
        unreadBtn.setOnClickListener(v1 -> updateNotifications(false));
        readBtn.setOnClickListener(btnView -> updateNotifications(true));
        updateNotifications(true);

        return v;
    }

    public void updateNotifications(Boolean read) {
        this.read = read;
        ClientUtils.notificationService.findByUserId(TokenUtils.getId(), null, read).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notification>> call, @NonNull Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    List<Notification> notifications = response.body();
                    if (notifications != null) {
                        adapter.clear();
                        adapter.addAll(notifications);
                    }
                } else {
                    Log.e("(¬‿¬)", "updateNotificaitonCount(): " + response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notification>> call, @NonNull Throwable t) {
                Log.e("(¬‿¬)", t.getMessage() == null ? "Failed getting unread notifications" : t.getMessage());
            }
        });
    }
}