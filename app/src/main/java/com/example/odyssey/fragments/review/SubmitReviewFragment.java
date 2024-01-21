package com.example.odyssey.fragments.review;

import android.media.Rating;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.HostReview;
import com.example.odyssey.model.reviews.Review;
import com.example.odyssey.model.users.User;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SubmitReviewFragment extends Fragment {

    private Long id;
    private String type;
    private User loggedUser;
    private Accommodation accommodation;
    private User host;

    public SubmitReviewFragment() {

    }

    public static SubmitReviewFragment newInstance(String param1, String param2) {
        SubmitReviewFragment fragment = new SubmitReviewFragment();
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
        id = (Long) getArguments().getSerializable("id");
        type = (String) getArguments().getSerializable("type");
        getCurrentUser();
        if(type.equals("ACCOMMODATION")) {
            getAccommodation();
        } else if(type.equals("HOST")){
            getReviewHost();
        }
        else{
            Log.e("SubmitReviewFragment", "Invalid type");
        }
        return inflater.inflate(R.layout.fragment_submit_review, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton sendReviewButton = getView().findViewById(R.id.review_submit_button);
        sendReviewButton.setOnClickListener(v -> submitReview());
    }

    private void submitReview(){
        if(!isReviewDataValid()) return;;
        RatingBar ratingBar = getView().findViewById(R.id.review_rating_bar);
        Double rating = Double.valueOf(ratingBar.getRating());
        TextInputEditText commentInput = getView().findViewById(R.id.review_comment_input);
        String comment = commentInput.getText().toString();

        if(type.equals("ACCOMMODATION")){
            AccommodationReview review = new AccommodationReview(null, rating,  comment, LocalDateTime.now(), loggedUser, Review.Status.REQUESTED, accommodation);
            sendAccommodationReview(review);
        }
        else if (type.equals("HOST")){
            HostReview review = new HostReview(null, rating,  comment, LocalDateTime.now(), loggedUser, Review.Status.REQUESTED, host);
            sendHostReview(review);

        }
        else{
            Log.e("SubmitReviewFragment", "Invalid type");
        }
    }

    private Boolean isReviewDataValid() {
        RatingBar ratingBar = getView().findViewById(R.id.review_rating_bar);
        Double rating = Double.valueOf(ratingBar.getRating());
        if (rating == null || rating < 0 || rating > 5) {
            return false;
        } else {
            return true;
        }
    }

    private void sendAccommodationReview(AccommodationReview review){
        Call<ResponseBody> createReviewCall = ClientUtils.reviewService.createAccommodationReview(review);
        createReviewCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireActivity(), "Successfully created a review!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireActivity(), "Unexpected error while creating a review", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(requireActivity(), "Unable to connect to the server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void sendHostReview(HostReview review){
        Call<ResponseBody> createReviewCall = ClientUtils.reviewService.createHostReview(review);
        createReviewCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireActivity(), "Successfully created a review!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireActivity(), "Unexpected error while creating a review", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(requireActivity(), "Unable to connect to the server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void getAccommodation(){
        Call<Accommodation> getUserCall = ClientUtils.accommodationService.findById(id);
        getUserCall.enqueue(new Callback<Accommodation>() {
            @Override
            public void onResponse(@NonNull Call<Accommodation> call, @NonNull Response<Accommodation> response) {
                if (response.isSuccessful()) {
                    accommodation = response.body();
                } else {
                    accommodation = null;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Accommodation> call, @NonNull Throwable t) {
                accommodation = null;
                t.printStackTrace();
            }
        });
    }

    private void getReviewHost(){
        Call<User> getUserCall = ClientUtils.userService.findById(id);
        getUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    host = response.body();
                } else {
                    host = null;
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                loggedUser = null;
                t.printStackTrace();
            }
        });
    }
    private void getCurrentUser() {
        Call<User> getUserCall = ClientUtils.userService.findById(TokenUtils.getId(requireContext()));
        getUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    loggedUser = response.body();
                } else {
                    loggedUser = null;
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                loggedUser = null;
                t.printStackTrace();
            }
        });
    }
}