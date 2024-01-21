package com.example.odyssey.fragments.accommodation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.odyssey.R;
import com.example.odyssey.adapters.AccommodationRequestAdapter;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationManagementFragment extends Fragment {
    AccommodationRequestAdapter adapter;
    ListView requestsView;
    List<AccommodationRequest> requests = new ArrayList<>();
    Boolean accepted = null;

    public AccommodationManagementFragment() {
    }

    public static AccommodationManagementFragment newInstance() {
        return new AccommodationManagementFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_accommodation_management, container, false);

        requestsView = v.findViewById(R.id.requests_list);

        adapter = new AccommodationRequestAdapter(requireContext(), v, new ArrayList<>());
        requestsView.setAdapter(adapter);

        MaterialButton allBtn = v.findViewById(R.id.requested_toggle);
        MaterialButton acceptedBtn = v.findViewById(R.id.accepted_toggle);
        MaterialButton declinedBtn = v.findViewById(R.id.declined_toggle);
        allBtn.setOnClickListener(btnView -> {
            accepted = null;
            updateData();
        });
        allBtn.setChecked(accepted == null);
        acceptedBtn.setOnClickListener(v1 -> {
            accepted = true;
            updateData();
        });
        declinedBtn.setOnClickListener(btnView -> {
            accepted = false;
            updateData();
        });

        updateData();
        updateData();
        return v;
    }

    private void updateData() {
        String status = accepted == null ? "REQUESTED" : accepted ? "ACCEPTED" : "DECLINED";
        ClientUtils.accommodationRequestService.findByStatus(status).enqueue(new Callback<List<AccommodationRequest>>() {
            @Override
            public void onResponse(@NonNull Call<List<AccommodationRequest>> call, @NonNull Response<List<AccommodationRequest>> response) {
                if (response.isSuccessful()) {
                    List<AccommodationRequest> updated = response.body();
                    if (updated != null) {
                        updated.sort((n1, n2) -> n2.getSubmissionDate().compareTo(n1.getSubmissionDate()));
                        requests.clear();
                        requests.addAll(updated);
                        adapter.clear();
                        adapter.addAll(updated);
                    }
                } else {
                    String error = ClientUtils.getError(response, "Requests could not be fetched");
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    Log.e("AccommodationManagementFragment", "updateData(): " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AccommodationRequest>> call, @NonNull Throwable t) {
                Log.e("AccommodationManagementFragment", t.getMessage() == null ? "Failed fetching requests" : t.getMessage());
            }
        });
    }
}