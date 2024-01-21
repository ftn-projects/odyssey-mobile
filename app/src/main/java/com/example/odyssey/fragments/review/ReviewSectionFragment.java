package com.example.odyssey.fragments.review;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.utils.TokenUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewSectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewSectionFragment extends Fragment {

    private Long id;
    private String type;

    public ReviewSectionFragment() {
        // Required empty public constructor
    }


    public static ReviewSectionFragment newInstance(String param1, String param2) {
        ReviewSectionFragment fragment = new ReviewSectionFragment();
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
        return inflater.inflate(R.layout.fragment_review_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (type.equals("ACCOMMODATION")) {
            getAccommodationReviewRatings();
        } else if (type.equals("HOST")) {
            getHostReviewRatings();
        } else {
            Log.e("SubmitReviewFragment", "Invalid type");
        }
        if (TokenUtils.getRole()==null || ( TokenUtils.getRole().equals("GUEST") || TokenUtils.getRole().equals("USER") )) {
            createReviewCreation();
        }
        createReviewList();
    }


    private void getAccommodationReviewRatings() {
        Call<ArrayList<Integer>> ratingsCall = ClientUtils.reviewService.getAccommodationRatings(id);
        ratingsCall.enqueue(new Callback<ArrayList<Integer>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Integer>> call, @NonNull Response<ArrayList<Integer>> response) {
                if (response.isSuccessful()) {
                    createReviewSummary(response.body());
                } else {
                    Log.e("REZ", "Unable to get ratings");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Integer>> call, @NonNull Throwable t) {
                Log.e("REZ", "Error while trying to get ratings");
                t.printStackTrace();
            }
        });
    }

    private void getHostReviewRatings() {
        Call<ArrayList<Integer>> ratingsCall = ClientUtils.reviewService.getHostRatings(id);
        ratingsCall.enqueue(new Callback<ArrayList<Integer>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Integer>> call, @NonNull Response<ArrayList<Integer>> response) {
                if (response.isSuccessful()) {
                    createReviewSummary(response.body());
                } else {
                    Log.e("REZ", "Unable to get ratings");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Integer>> call, @NonNull Throwable t) {
                Log.e("REZ", "Error while trying to get ratings");
                t.printStackTrace();
            }
        });
    }

    private void createReviewList() {
        LinearLayout reviewListContainer = getView().findViewById(R.id.review_section_reviews_container);
        ReviewsContainerFragment reviewListFragment = new ReviewsContainerFragment();
        Bundle args = new Bundle();
        args.putSerializable("id", id);
        args.putSerializable("type", type);
        reviewListFragment.setArguments(args);
        getChildFragmentManager().beginTransaction()
                .add(reviewListContainer.getId(), reviewListFragment)
                .commit();
    }

    private void createReviewCreation() {
        LinearLayout reviewCreationContainer = getView().findViewById(R.id.submit_review_container);
        SubmitReviewFragment submitReviewFragment = new SubmitReviewFragment();
        Bundle args = new Bundle();
        args.putSerializable("id", id);
        args.putSerializable("type", type);
        submitReviewFragment.setArguments(args);
        getChildFragmentManager().beginTransaction()
                .add(reviewCreationContainer.getId(), submitReviewFragment)
                .commit();
    }

    private void createReviewSummary(List<Integer> ratings) {
        LinearLayout reviewsSummaryContainer = getView().findViewById(R.id.summary_reviews_container);
        ReviewsSummary reviewsSummary = new ReviewsSummary();
        Bundle args = new Bundle();
        args.putSerializable("ratings", (ArrayList<Integer>) ratings);
        reviewsSummary.setArguments(args);
        getChildFragmentManager().beginTransaction()
                .add(reviewsSummaryContainer.getId(), reviewsSummary)
                .commit();
    }
}