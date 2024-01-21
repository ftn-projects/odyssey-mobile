package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.HostReview;
import com.example.odyssey.model.reviews.Review;
import com.example.odyssey.utils.TokenUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuestReviewsView extends Fragment {

    private Long guestId;

    private List<AccommodationReview> accommodationReviews;
    LinearLayout reviewsContainer;
    public GuestReviewsView() {

    }

    public static GuestReviewsView newInstance() {
        GuestReviewsView fragment = new GuestReviewsView();
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
        View view = inflater.inflate(R.layout.fragment_guest_reviews_view, container, false);
        reviewsContainer = view.findViewById(R.id.guest_reviews_view_container);
        setGuestId(TokenUtils.getId(requireContext()));
        return view;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
        reviewsContainer.removeAllViews();
        getGuestAccommodationReviews();
        getGuestHostReviews();
    }

    private void getGuestHostReviews(){
        ArrayList<Review.Status> listTypes = new ArrayList<>(Arrays.asList(Review.Status.ACCEPTED));
        Call<ArrayList<HostReview>> call = ClientUtils.reviewService.getAllHostReviews(null,guestId,listTypes);
        call.enqueue(new Callback<ArrayList<HostReview>>() {
            @Override
            public void onResponse(Call<ArrayList<HostReview>> call, Response<ArrayList<HostReview>> response) {
                if (response.code() == 200) {
                    List<HostReview> reviews = response.body();
                    if (reviews != null) {
                        populateHostReviews(reviews);

                    } else {
                        Log.d("REZ", "Bad");
                    }
                }
                else{
                    String error = ClientUtils.getError(response, "Error while getting host reviews");
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<HostReview>> call, Throwable t) {
                Log.d("REZ",t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void getGuestAccommodationReviews() {
        ArrayList<Review.Status> listTypes = new ArrayList<>(Arrays.asList(Review.Status.ACCEPTED));
        Call<ArrayList<AccommodationReview>> call = ClientUtils.reviewService.getAllAccommodationReviews(null,guestId,listTypes);
        call.enqueue(new Callback<ArrayList<AccommodationReview>>() {
            @Override
            public void onResponse(Call<ArrayList<AccommodationReview>> call, Response<ArrayList<AccommodationReview>> response) {
                if (response.code() == 200) {
                    accommodationReviews = response.body();
                    if (accommodationReviews != null) {
                        populateAccommodationReviews(accommodationReviews);

                    } else {
                        Log.d("REZ", "Bad");
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<AccommodationReview>> call, Throwable t) {
                Log.d("REZ",t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void populateHostReviews(List<HostReview> reviews){
        for (HostReview review : reviews) {
            HostReviewCard card = new HostReviewCard();
            Bundle args = new Bundle();
            args.putSerializable("hostReview", review);
            card.setArguments(args);

            getChildFragmentManager().beginTransaction()
                    .add(reviewsContainer.getId(), card)
                    .commit();
        }
    }

    private void populateAccommodationReviews(List<AccommodationReview> reviews){
        for (AccommodationReview review : reviews) {
            AccommodationReviewCard card = new AccommodationReviewCard();
            Bundle args = new Bundle();
            args.putSerializable("accommodationReview", review);
            card.setArguments(args);

            getChildFragmentManager().beginTransaction()
                    .add(reviewsContainer.getId(), card)
                    .commit();
        }
    }
}