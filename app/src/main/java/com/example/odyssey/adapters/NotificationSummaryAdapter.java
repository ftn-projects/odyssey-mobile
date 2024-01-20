package com.example.odyssey.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssey.R;
import com.example.odyssey.activities.MainActivity;
import com.example.odyssey.model.notifications.Notification;

import java.util.List;

public class NotificationSummaryAdapter extends ArrayAdapter<Notification> {
    View rootView;
    public NotificationSummaryAdapter(Context context, View rootView, @NonNull List<Notification> notifications) {
        super(context, 0, notifications);
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

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_summary_item, parent, false);
        }

        convertView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putSerializable("notification", notification);
            Navigation.findNavController(rootView).navigate(R.id.nav_accommodation_details);
        });

        TextView title = convertView.findViewById(R.id.notification_title);
        TextView description = convertView.findViewById(R.id.notification_short_description);
        title.setText(notification.getShortTitle());
        description.setText(notification.getShortText());

        return convertView;
    }

}
