package com.example.odyssey.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.model.users.UserWithReports;
import com.example.odyssey.utils.TokenUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationRequestAdapter extends ArrayAdapter<AccommodationRequest> {
    Context context;
    View rootView;

    public AccommodationRequestAdapter(Context context, View rootView, @NonNull List<AccommodationRequest> requests) {
        super(context, 0, requests);
        this.context = context;
        this.rootView = rootView;
    }

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        AccommodationRequest request = getItem(position);
        if (request == null) {
            Log.e("AccommodationRequestAdapter", "Accommodation request is null");
            return convertView;
        }

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.accommodation_request_item, parent, false);

        TextView titleView = convertView.findViewById(R.id.title);
        TextView typeView = convertView.findViewById(R.id.type);
        TextView dateView = convertView.findViewById(R.id.date);

        titleView.setText(request.getDetails().getTitle());
        typeView.setText(request.getType().toString());
        dateView.setText(formatter.format(request.getSubmissionDate()));

        convertView.setOnClickListener(view1 -> {
            Bundle args = new Bundle();
            args.putSerializable("request", request);
            Navigation.findNavController(rootView).navigate(R.id.nav_accommodation_request_details, args);
        });

        return convertView;
    }
}
