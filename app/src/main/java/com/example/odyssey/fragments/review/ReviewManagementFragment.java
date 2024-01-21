package com.example.odyssey.fragments.review;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.odyssey.R;
import com.example.odyssey.adapters.ReviewManagementAdapter;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.reviews.Review;
import com.example.odyssey.model.users.UserWithReports;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewManagementFragment extends Fragment {
    ReviewManagementAdapter adapter;
    ListView reviewsView;
    List<Review> reviews = new ArrayList<>();
    String search;
    Boolean reported = false;
    String status = null;

    public ReviewManagementFragment() {
    }

    public static ReviewManagementFragment newInstance() {
        return new ReviewManagementFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    MaterialButton requested;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review_management, container, false);

        reviewsView = v.findViewById(R.id.review_list);

        adapter = new ReviewManagementAdapter(requireContext(), v, new ArrayList<>());
        reviewsView.setAdapter(adapter);

        requested = v.findViewById(R.id.requested_toggle);
        MaterialButton acceptedBtn = v.findViewById(R.id.active_toggle);
        MaterialButton declinedBtn = v.findViewById(R.id.blocked_toggle);
        requested.setOnClickListener(btnView -> {
            status = "REQUESTED";
            filter();
        });
        acceptedBtn.setOnClickListener(v1 -> {
            status = "ACCEPTED";
            filter();
        });
        declinedBtn.setOnClickListener(btnView -> {
            status = "DECLINED";
            filter();
        });

        requested.setChecked(true);
        status = "REQUESTED";

        TextInputEditText searchInput = v.findViewById(R.id.input_search);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search = s.toString().toLowerCase();
                filter();
            }
        });

        updateData();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        requested.setChecked(true);
        status = "REQUESTED";
        search = null;
        updateData();
    }

    private void filter() {
        List<Review> filtered = reviews.stream().filter(r ->
                (!status.equals("REQUESTED") || r.getStatus().equals(Review.Status.REQUESTED)) &&
                        (!status.equals("ACCEPTED") || r.getStatus().equals(Review.Status.ACCEPTED)) &&
                        (!status.equals("DECLINED") || r.getStatus().equals(Review.Status.DECLINED)) &&
                        (search == null || (r.getComment().toLowerCase().contains(search)))
        ).collect(Collectors.toList());

        adapter.clear();
        adapter.addAll(filtered);
    }

    private void updateData() {
        ClientUtils.reviewService.getAll(null, null, null).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(@NonNull Call<List<Review>> call, @NonNull Response<List<Review>> response) {
                if (response.isSuccessful()) {
                    List<Review> updated = response.body();
                    if (updated != null) {
                        updated.sort((n1, n2) -> n2.getSubmissionDate().compareTo(n1.getSubmissionDate()));
                        reviews.clear();
                        reviews.addAll(updated);
                        filter();
                    }
                } else {
                    String error = ClientUtils.getError(response, "Users could not be fetched");
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    Log.e("ReviewManagementFragment", "updateData(): " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Review>> call, @NonNull Throwable t) {
                Log.e("ReviewManagementFragment", t.getMessage() == null ? "Failed getting reviews" : t.getMessage());
            }
        });
    }
}