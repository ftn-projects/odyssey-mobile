package com.example.odyssey.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.notifications.Notification;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationSummaryAdapter extends ArrayAdapter<Notification> {
    View rootView;
    Context context;

    public NotificationSummaryAdapter(Context context, View rootView, @NonNull List<Notification> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.rootView = rootView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Notification notification = getItem(position);
        if (notification == null) {
            Log.e("NotificationSummaryAdapter", "Notification is null");
            return convertView;
        }

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_summary_item, parent, false);

        convertView.setOnClickListener(v -> {
            readNotification(notification.getId());
            Bundle args = new Bundle();
            args.putSerializable("notification", notification);
            Navigation.findNavController(rootView).navigate(R.id.nav_generic_notification, args);
        });

        int background = R.drawable.unread_notification_background;
        if (notification.getRead())
            background = R.drawable.read_notification_background;
        convertView.setBackground(AppCompatResources.getDrawable(context, background));

        TextView title = convertView.findViewById(R.id.notification_title);
        title.setText(notification.getShortTitle());

        return convertView;
    }

    private void readNotification(Long id) {
        ClientUtils.notificationService.updateRead(id, true).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful())
                    Log.e("NotificationSummaryAdapter", "readNotification(): " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("NotificationSummaryAdapter", "readNotification(): " + t.getMessage());
            }
        });
    }
}
