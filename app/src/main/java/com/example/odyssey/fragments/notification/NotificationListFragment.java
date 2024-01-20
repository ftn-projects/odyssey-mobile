package com.example.odyssey.fragments.notification;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.odyssey.R;
import com.example.odyssey.adapters.NotificationSummaryAdapter;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.clients.StompClient;
import com.example.odyssey.model.notifications.Notification;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationListFragment extends Fragment {
    StompClient stompClient = null;
    NotificationSummaryAdapter adapter;
    List<Notification> notifications = new ArrayList<>();
    ListView notificationsView;
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
        stompClient.subscribe("/topic/notifications", this::updateNotifications);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification_list, container, false);
        notificationsView = v.findViewById(R.id.notifications_list_view);

        adapter = new NotificationSummaryAdapter(requireContext(), v, new ArrayList<>());
        notificationsView.setAdapter(adapter);

        MaterialButton allBtn = v.findViewById(R.id.notification_all_toggle);
        MaterialButton readBtn = v.findViewById(R.id.notification_read_toggle);
        MaterialButton unreadBtn = v.findViewById(R.id.notification_unread_toggle);
        allBtn.setOnClickListener(btnView -> filterAccommodations(null));
        allBtn.setChecked(true);
        unreadBtn.setOnClickListener(v1 -> filterAccommodations(false));
        readBtn.setOnClickListener(btnView -> filterAccommodations(true));

        updateNotifications();

        return v;
    }

    private void filterAccommodations(Boolean read) {
        this.read = read;
        List<Notification> filtered = new ArrayList<>();

        if (read == null) filtered.addAll(notifications);
        else filtered.addAll(notifications.stream().filter(
                n -> n.getRead() == read).collect(Collectors.toList()));

        adapter.clear();
        adapter.addAll(filtered);
    }

    private void updateNotifications() {
        ClientUtils.notificationService.findByUserId(TokenUtils.getId(), null, null).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notification>> call, @NonNull Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    List<Notification> updatedNotifications = response.body();
                    if (updatedNotifications != null) {
                        notifications.clear();
                        updatedNotifications.sort((n1, n2) -> n2.getDate().compareTo(n1.getDate()));
                        notifications.addAll(updatedNotifications);
                        filterAccommodations(read);
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