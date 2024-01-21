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

public class HostReviewsView extends Fragment {
    private LinearLayout reviewsContainer;
    public HostReviewsView() {

    }

    public static HostReviewsView newInstance(String param1, String param2) {
        HostReviewsView fragment = new HostReviewsView();
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

    public void loadAll() {
        reviewsContainer.removeAllViews();
        getAccommodationReviews();
        getHostReviews();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_host_reviews_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reviewsContainer = view.findViewById(R.id.host_reviews_view_container);
        loadAll();
    }

    private void getAccommodationReviews(){
        ArrayList<Review.Status> statuses = new ArrayList<>();
        statuses.add(Review.Status.ACCEPTED);
        Call<ArrayList<AccommodationReview>> call = ClientUtils.reviewService.getAllAccommodationReviewsByHostId(TokenUtils.getId(requireContext()), statuses);
        call.enqueue(new Callback<ArrayList<AccommodationReview>>() {
            @Override
            public void onResponse(Call<ArrayList<AccommodationReview>> call, Response<ArrayList<AccommodationReview>> response) {
                if (response.code() == 200) {
                    List<AccommodationReview> reviews = response.body();
                    if (reviews != null) {
                        populateAccommodationReviews(reviews);

                    } else {
                        Log.d("REZ", "Bad");
                    }
                }
                else{
                    String error = ClientUtils.getError(response, "Error while getting reviews");
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<AccommodationReview>> call, Throwable t) {
                Log.d("REZ",t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void getHostReviews(){
        ArrayList<Review.Status> statuses = new ArrayList<>();
        statuses.add(Review.Status.ACCEPTED);
        Call<ArrayList<HostReview>> call = ClientUtils.reviewService.getAllHostReviews(TokenUtils.getId(requireContext()), null,statuses);
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
                    String error = ClientUtils.getError(response, "Error while getting reviews");
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<HostReview>> call, Throwable t) {
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