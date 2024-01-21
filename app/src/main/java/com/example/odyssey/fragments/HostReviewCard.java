package com.example.odyssey.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.HostReview;
import com.example.odyssey.utils.TokenUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HostReviewCard extends Fragment {
    private HostReview hostReview;
    ImageView userImage;
    TextView usernameTextView;
    TextView ratingTextView;
    TextView commentTextView;
    TextView dateTextView;
    ImageView flagIcon;
    ImageView trashIcon;

    TextView commentedOn;

    public HostReviewCard() {
        // Required empty public constructor
    }


    public static HostReviewCard newInstance(String param1, String param2) {
        HostReviewCard fragment = new HostReviewCard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host_review_card, container, false);
        hostReview = (HostReview) getArguments().getSerializable("hostReview");

        userImage = view.findViewById(R.id.accommodation_review_profile_image);
        commentedOn = view.findViewById(R.id.commentedOnHost);
        commentedOn.setText("Commented on host " + hostReview.getHost().getName() + " " + hostReview.getHost().getSurname());
        String imagePath = loadUserImage();
        Glide.with(getContext()).load(imagePath).into(userImage);
        usernameTextView = view.findViewById(R.id.accommodation_review_username);
        usernameTextView.setText(hostReview.getSubmitter().getName() + " " + hostReview.getSubmitter().getSurname());

        ratingTextView = view.findViewById(R.id.accommodation_review_rating);
        ratingTextView.setText(hostReview.getRating().toString());

        commentTextView = view.findViewById(R.id.accommodation_review_comment);
        commentTextView.setText(hostReview.getComment());

        dateTextView = view.findViewById(R.id.accommodation_review_date);

        LocalDateTime submissionDate = hostReview.getSubmissionDate();

        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                submissionDate.toInstant(ZoneOffset.ofHoursMinutes(1,0)).toEpochMilli(),
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
        );
        dateTextView.setText(relativeTime);
        trashIcon = view.findViewById(R.id.accommodation_review_trash_icon);
        flagIcon = view.findViewById(R.id.accommodation_review_flag_icon);

        if(TokenUtils.getId(requireContext()) != null && hostReview.getSubmitter().getId() == TokenUtils.getId(requireContext())){
            trashIcon.setVisibility(View.VISIBLE);
        }
        else trashIcon.setVisibility(View.GONE);

        if(TokenUtils.getId(requireContext()) != null && hostReview.getHost().getId() == TokenUtils.getId(requireContext()))
        {
            flagIcon.setVisibility(View.VISIBLE);
        }
        else flagIcon.setVisibility(View.GONE);

        trashIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog("Are you sure you want to delete this review?", new Runnable() {
                    @Override
                    public void run() {
                        // Perform the delete action
                        deleteReview();
                    }
                });
            }
        });

        flagIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog("Are you sure you want to report this review?", new Runnable() {
                    @Override
                    public void run() {
                        // Perform the report action
                        reportReview();
                    }
                });
            }
        });

        return view;
    }

    private void reportReview() {

        Call<ResponseBody> call = ClientUtils.reviewService.reportHostReview(hostReview.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("HostReviewCard", "onResponse: " + response.code());
                if (response.code() == 200) {
                    flagIcon.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Review reported", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("HostReviewCard", "onFailure: " + t.getMessage());
                Toast.makeText(getContext(), "Error reporting review", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteReview() {
        Call<ResponseBody> call = ClientUtils.reviewService.deleteHostReview(hostReview.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("HostReviewCard", "onResponse: " + response.code());
                if (response.code() == 200) {
                    trashIcon.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Review deleted", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Error deleting review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("HostReviewCard", "onFailure: " + t.getMessage());
                Toast.makeText(getContext(), "Error deleting review", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmationDialog(String message, final Runnable onYesAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onYesAction.run();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public String loadUserImage(){
        return ClientUtils.SERVICE_API_PATH + "users/image/" + hostReview.getSubmitter().getId();
    }
}