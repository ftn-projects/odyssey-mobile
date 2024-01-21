package com.example.odyssey.fragments.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.Review;

import java.time.format.DateTimeFormatter;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccommodationReviewDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccommodationReviewDetails extends Fragment {


    public AccommodationReviewDetails() {
        // Required empty public constructor
    }

    private AccommodationReview review;

    private boolean isReviewShown = false;
    public final String ARG_NOTIFICATION = "review";

    public static AccommodationReviewDetails newInstance() {
        return new AccommodationReviewDetails();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_NOTIFICATION))
            review = (AccommodationReview) getArguments().getSerializable(ARG_NOTIFICATION);

        if (review == null) throw new RuntimeException("Review is null");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_accommodation_review_details, container, false);
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView notificationTitle = view.findViewById(R.id.notification_title);
        notificationTitle.setText("Accommodation review");

        LinearLayout reviewSection = view.findViewById(R.id.notification_review_section);

        ImageView notificationImage = view.findViewById(R.id.notification_profile_image);
        String imagePath = ClientUtils.SERVICE_API_PATH + "users/image/" + review.getSubmitter().getId();
        Glide.with(getContext()).load(imagePath).into(notificationImage);

        TextView notificationReviewer = view.findViewById(R.id.notification_reviewer);
        notificationReviewer.setText(review.getSubmitter().getName() + " " + review.getSubmitter().getSurname());

        TextView notificationRating = view.findViewById(R.id.notification_rating);
        notificationRating.setText(review.getRating().toString());

        TextView notificationComment = view.findViewById(R.id.notification_comment);
        notificationComment.setText(review.getComment());

        TextView notificationDate = view.findViewById(R.id.notification_time);
        notificationDate.setText(formatter.format(review.getSubmissionDate()));

        reviewSection.setVisibility(View.VISIBLE);

        Button activate = view.findViewById(R.id.activate_button);
        activate.setOnClickListener(v -> accept());
        Button decline = view.findViewById(R.id.deactivate_button);
        decline.setOnClickListener(v -> decline());
        Button dismiss = view.findViewById(R.id.dismiss_button);
        dismiss.setOnClickListener(v -> dismiss());

        activate.setVisibility(review.getStatus().equals(Review.Status.REQUESTED) || review.getStatus().equals(Review.Status.DECLINED) ? View.VISIBLE : View.GONE);
        decline.setVisibility(!review.getStatus().equals(Review.Status.DECLINED) ? View.VISIBLE : View.GONE);
        dismiss.setVisibility(review.getStatus().equals(Review.Status.REPORTED) ? View.VISIBLE : View.GONE);
    }

    private void accept() {
        ClientUtils.reviewService.accept(review.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    String message = ClientUtils.getError(response, "Review could not be accepted.");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getContext(), "Review accepted.", Toast.LENGTH_LONG).show();
                Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigateUp();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private void decline() {
        ClientUtils.reviewService.decline(review.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    String message = ClientUtils.getError(response, "Review could not be declined.");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getContext(), "Review declined.", Toast.LENGTH_LONG).show();
                Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigateUp();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private void dismiss() {
        ClientUtils.reviewService.dismiss(review.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    String message = ClientUtils.getError(response, "Review could not be dismissed.");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getContext(), "Review dismissed.", Toast.LENGTH_LONG).show();
                Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigateUp();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }
}