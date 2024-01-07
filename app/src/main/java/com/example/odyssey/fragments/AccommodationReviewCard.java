package com.example.odyssey.fragments;

import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.reviews.AccommodationReview;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccommodationReviewCard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccommodationReviewCard extends Fragment {
    private AccommodationReview accommodationReview;
    ImageView userImage;
    TextView usernameTextView;
    TextView ratingTextView;
    TextView commentTextView;
    TextView dateTextView;
    public AccommodationReviewCard() {
        // Required empty public constructor
    }
    public static AccommodationReviewCard newInstance() {
        AccommodationReviewCard fragment = new AccommodationReviewCard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accommodation_review_card, container, false);
        accommodationReview = (AccommodationReview) getArguments().getSerializable("accommodationReview");
        userImage = view.findViewById(R.id.accommodation_review_profile_image);
        String imagePath = loadUserImage();
        Glide.with(getContext()).load(imagePath).into(userImage);
        usernameTextView = view.findViewById(R.id.accommodation_review_username);
        usernameTextView.setText(accommodationReview.getSubmitter().getName() + " " + accommodationReview.getSubmitter().getSurname());

        ratingTextView = view.findViewById(R.id.accommodation_review_rating);
        ratingTextView.setText(accommodationReview.getRating().toString());

        commentTextView = view.findViewById(R.id.accommodation_review_comment);
        commentTextView.setText(accommodationReview.getComment());

        dateTextView = view.findViewById(R.id.accommodation_review_date);

        LocalDateTime submissionDate = accommodationReview.getSubmissionDate();

        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                submissionDate.toInstant(ZoneOffset.ofHoursMinutes(1,0)).toEpochMilli(),
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
        );
        dateTextView.setText(relativeTime);
        return view;
    }

    public String loadUserImage(){
        return ClientUtils.SERVICE_API_PATH + "users/image/" + accommodationReview.getSubmitter().getId();
    }
}