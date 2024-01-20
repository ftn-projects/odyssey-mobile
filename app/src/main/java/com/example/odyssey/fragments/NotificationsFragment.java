package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.odyssey.R;

public class NotificationsFragment extends Fragment {
    ListView notifications;

    public NotificationsFragment() {
    }

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        notifications = v.findViewById(R.id.notifications_list_view);



        return v;
    }
}