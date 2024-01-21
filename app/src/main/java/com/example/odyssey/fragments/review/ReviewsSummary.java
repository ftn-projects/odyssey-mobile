package com.example.odyssey.fragments.review;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.odyssey.R;

import java.util.List;


public class ReviewsSummary extends Fragment {

    private List<Integer> ratingsCount;

    public ReviewsSummary() {

    }



    public static ReviewsSummary newInstance(String param1, String param2) {
        ReviewsSummary fragment = new ReviewsSummary();
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
        View view = inflater.inflate(R.layout.fragment_reviews_summary, container, false);
        ratingsCount = (List<Integer>) getArguments().getSerializable("ratings");
        ProgressBar fiveStarBar = view.findViewById(R.id.progressBar5);
        ProgressBar fourStarBar = view.findViewById(R.id.progressBar4);
        ProgressBar threeStarBar = view.findViewById(R.id.progressBar3);
        ProgressBar twoStarBar = view.findViewById(R.id.progressBar2);
        ProgressBar oneStarBar = view.findViewById(R.id.progressBar1);
        TextView fiveStarCount = view.findViewById(R.id.reviewCount5);
        TextView fourStarCount = view.findViewById(R.id.reviewCount4);
        TextView threeStarCount = view.findViewById(R.id.reviewCount3);
        TextView twoStarCount = view.findViewById(R.id.reviewCount2);
        TextView oneStarCount = view.findViewById(R.id.reviewCount1);

        // Calculate total reviews
        int totalReviews = ratingsCount.stream().mapToInt(Integer::intValue).sum();

        // Calculate percentage for each rating
        int fiveStarPercentage = calculatePercentage(ratingsCount.get(4), totalReviews);
        int fourStarPercentage = calculatePercentage(ratingsCount.get(3), totalReviews);
        int threeStarPercentage = calculatePercentage(ratingsCount.get(2), totalReviews);
        int twoStarPercentage = calculatePercentage(ratingsCount.get(1), totalReviews);
        int oneStarPercentage = calculatePercentage(ratingsCount.get(0), totalReviews);

        // Set text and progress for each rating
        fiveStarCount.setText(ratingsCount.get(4).toString());
        fourStarCount.setText(ratingsCount.get(3).toString());
        threeStarCount.setText(ratingsCount.get(2).toString());
        twoStarCount.setText(ratingsCount.get(1).toString());
        oneStarCount.setText(ratingsCount.get(0).toString());

        fiveStarBar.setProgress(fiveStarPercentage);
        fourStarBar.setProgress(fourStarPercentage);
        threeStarBar.setProgress(threeStarPercentage);
        twoStarBar.setProgress(twoStarPercentage);
        oneStarBar.setProgress(oneStarPercentage);

        return view;
    }

    // Helper method to calculate percentage
    private int calculatePercentage(int ratingCount, int totalReviews) {
        if (totalReviews == 0) {
            return 0;
        }
        return (ratingCount * 100) / totalReviews;
    }
}