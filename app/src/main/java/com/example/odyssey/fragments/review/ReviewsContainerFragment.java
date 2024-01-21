package com.example.odyssey.fragments.review;

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
import com.example.odyssey.fragments.AccommodationReviewCard;
import com.example.odyssey.fragments.HostReviewCard;
import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.HostReview;
import com.example.odyssey.model.reviews.Review;
import com.example.odyssey.model.stats.AccommodationTotalStats;
import com.example.odyssey.model.stats.TotalStats;
import com.example.odyssey.utils.TokenUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReviewsContainerFragment extends Fragment {


    private Long id;
    private String type;

    public ReviewsContainerFragment() {

    }

    public static ReviewsContainerFragment newInstance(String param1, String param2) {
        ReviewsContainerFragment fragment = new ReviewsContainerFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        id = (Long) getArguments().getSerializable("id");
        type = (String) getArguments().getSerializable("type");
        return inflater.inflate(R.layout.fragment_reviews_container, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (id == null) {
            Log.e("ReviewsContainerFragment", "id is null");
        }

        if (type == "HOST") {
            getHostReviews();
        } else if (type == "ACCOMMODATION") {
            getAccommodationReviews();
        } else {
            Log.e("ReviewsContainerFragment", "Invalid type");
        }

    }

    private void getAccommodationReviews() {
        ArrayList<Review.Status> statuses = new ArrayList<>();
        statuses.add(Review.Status.ACCEPTED);
        Call<ArrayList<AccommodationReview>> call = ClientUtils.reviewService.getAllAccommodationReviews(id, null, statuses);

        call.enqueue(new Callback<ArrayList<AccommodationReview>>() {
            @Override
            public void onResponse(Call<ArrayList<AccommodationReview>> call, Response<ArrayList<AccommodationReview>> response) {
                if (response.isSuccessful()) {
                    populateAccommodationReviews(response.body());
                }else{
                    String error = ClientUtils.getError(response, "Error while getting accommodation reviews");
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AccommodationReview>> call, Throwable t) {
                Toast.makeText(getContext(), "Error while fetching stats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getHostReviews() {
        ArrayList<Review.Status> statuses = new ArrayList<>();
        statuses.add(Review.Status.ACCEPTED);
        Call<ArrayList<HostReview>> call = ClientUtils.reviewService.getAllHostReviews(id, null, statuses);

        call.enqueue(new Callback<ArrayList<HostReview>>() {
            @Override
            public void onResponse(Call<ArrayList<HostReview>> call, Response<ArrayList<HostReview>> response) {
                if (response.isSuccessful()) {
                    populateHostReviews(response.body());
                }else{
                    String error = ClientUtils.getError(response, "Errror while getting host reviews");
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<HostReview>> call, Throwable t) {
                Toast.makeText(getContext(), "Error while fetching stats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateAccommodationReviews(List<AccommodationReview> reviews) {
        LinearLayout container = getView().findViewById(R.id.reviews_list_container);
        container.removeAllViews();
        for (AccommodationReview review : reviews) {
            AccommodationReviewCard card = new AccommodationReviewCard();
            Bundle args = new Bundle();
            args.putSerializable("accommodationReview", review);
            card.setArguments(args);

            getChildFragmentManager().beginTransaction()
                    .add(container.getId(), card)
                    .commit();
        }
    }

    private void populateHostReviews(List<HostReview> reviews) {
        LinearLayout container = getView().findViewById(R.id.reviews_list_container);
        container.removeAllViews();
        for (HostReview review : reviews) {
            HostReviewCard card = new HostReviewCard();
            Bundle args = new Bundle();
            args.putSerializable("hostReview", review);
            card.setArguments(args);

            getChildFragmentManager().beginTransaction()
                    .add(container.getId(), card)
                    .commit();
        }

    }
}