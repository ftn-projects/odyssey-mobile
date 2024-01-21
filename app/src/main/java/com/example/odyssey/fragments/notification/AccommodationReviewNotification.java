package com.example.odyssey.fragments.notification;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.notifications.Notification;
import com.example.odyssey.model.reservations.AccreditReservation;
import com.example.odyssey.model.reviews.AccommodationReview;

import java.time.ZoneOffset;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccommodationReviewNotification#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccommodationReviewNotification extends Fragment {


    public AccommodationReviewNotification() {
        // Required empty public constructor
    }
    private AccommodationReview review;
    private Notification notification;

    private boolean isReviewShown = false;
    public final String ARG_NOTIFICATION = "notification";

    public static AccommodationReviewNotification newInstance() {
        return new AccommodationReviewNotification();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null && getArguments().containsKey(ARG_NOTIFICATION))
            notification = (Notification) getArguments().getSerializable(ARG_NOTIFICATION);

        if(notification == null) throw new RuntimeException("Notification is null");
        else review = notification.getAccommodationReview();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_accommodation_review_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView notificationTitle = view.findViewById(R.id.notification_title);
        notificationTitle.setText(notification.getTitle());

        TextView notificationDetails = view.findViewById(R.id.notification_details);
        notificationDetails.setText(notification.getText());

        LinearLayout reviewSection = view.findViewById(R.id.notification_review_section);

        ImageView notificationImage = view.findViewById(R.id.notification_profile_image);
        String imagePath = ClientUtils.SERVICE_API_PATH + "users/image/" + notification.getAccommodationReview().getSubmitter().getId();
        Glide.with(getContext()).load(imagePath).into(notificationImage);

        TextView notificationReviewer = view.findViewById(R.id.notification_reviewer);
        notificationReviewer.setText(notification.getAccommodationReview().getSubmitter().getName() + " " + notification.getAccommodationReview().getSubmitter().getSurname());

        TextView notificationRating = view.findViewById(R.id.notification_rating);
        notificationRating.setText(notification.getAccommodationReview().getRating().toString());

        TextView notificationComment = view.findViewById(R.id.notification_comment);
        notificationComment.setText(notification.getAccommodationReview().getComment());

        TextView notificationDate = view.findViewById(R.id.notification_time);
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                notification.getDate().toInstant(ZoneOffset.ofHoursMinutes(1,0)).toEpochMilli(),
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
        );
        notificationDate.setText(relativeTime);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isReviewShown = !isReviewShown;
                reviewSection.setVisibility(isReviewShown ? View.VISIBLE : View.GONE);
            }
        });

        reviewSection.setVisibility(View.GONE);
    }
}