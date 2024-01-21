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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.Review;
import com.example.odyssey.model.users.UserWithReports;
import com.example.odyssey.utils.TokenUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReviewManagementAdapter extends ArrayAdapter<Review> {
    Context context;
    View rootView;

    public ReviewManagementAdapter(Context context, View rootView, @NonNull List<Review> reviews) {
        super(context, 0, reviews);
        this.context = context;
        this.rootView = rootView;
    }

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Review review = getItem(position);
        if (review == null) {
            Log.e("ReviewManagementAdapter", "Review is null");
            return convertView;
        }

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_management_item, parent, false);

        TextView nameView = convertView.findViewById(R.id.profile_name);
        TextView roleView = convertView.findViewById(R.id.profile_role);
        TextView dateView = convertView.findViewById(R.id.account_status);
        ImageView profileImage = convertView.findViewById(R.id.profile_image);

        String type = (review instanceof AccommodationReview) ? "Accommodation review" : "Host review";
        nameView.setText(review.getTitle());
        roleView.setText(type);
        dateView.setText(review.getStatus().toString());

        String imagePath = ClientUtils.SERVICE_API_PATH + "users/image/" + review.getSubmitter().getId();
        Glide.with(context).load(imagePath).into(profileImage);

        convertView.setOnClickListener(view1 -> {
            Bundle args = new Bundle();
            args.putSerializable("review", review);
            if (review instanceof AccommodationReview)
                Navigation.findNavController(rootView).navigate(R.id.nav_review_accommodation_details, args);
            else Navigation.findNavController(rootView).navigate(R.id.nav_review_host_details, args);
        });

        setStatusDesign(convertView, review.getStatus());

        return convertView;
    }

    private void setStatusDesign(View v, Review.Status status) {
        int backColor = 0, frontColor = 0;
        String text = "";
        switch (status) {
            case REQUESTED:
                backColor = R.color.pending_30;
                frontColor = R.color.pending;
                text = "REQUESTED";
                break;
            case ACCEPTED:
                backColor = R.color.approved_30;
                frontColor = R.color.approved;
                text = "ACCEPTED";
                break;
            case DECLINED:
                backColor = R.color.cancelled_30;
                frontColor = R.color.cancelled;
                text = "DECLINED";
                break;
            case REPORTED:
                backColor = R.color.approved_30;
                frontColor = R.color.approved;
                text = "REPORTED";
                break;
        }
        LinearLayout statusLayout = v.findViewById(R.id.status_layout);
        TextView statusView = v.findViewById(R.id.account_status);

        statusLayout.getBackground().setTint(ContextCompat.getColor(getContext(), backColor));
        statusView.setTextColor(ContextCompat.getColor(getContext(), frontColor));
        statusView.setText(text);
    }
}
