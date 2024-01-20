package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.odyssey.R;
import com.example.odyssey.model.notifications.Notification;

import java.time.format.DateTimeFormatter;

public class GenericNotification extends Fragment {

    TextView title, date, description;

    private Notification notification;
    public final String ARG_NOTIFICATION = "notification";
    public GenericNotification() {
    }

    public static GenericNotification newInstance() {
        return new GenericNotification();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null && getArguments().containsKey(ARG_NOTIFICATION))
            notification = (Notification) getArguments().getSerializable(ARG_NOTIFICATION);

        if(notification == null) throw new RuntimeException("Notification is null");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generic_notification, container, false);
        title = v.findViewById(R.id.notificationTitle);
        date = v.findViewById(R.id.notificationDate);
        description = v.findViewById(R.id.notificationDescription);

        title.setText(notification.getTitle());
        date.setText(notification.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        description.setText(notification.getText());

        return v;
    }
}