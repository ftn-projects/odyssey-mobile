package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.Review;
import com.example.odyssey.utils.TokenUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostAccommodationsView extends Fragment {


    public HostAccommodationsView() {
        // Required empty public constructor
    }
    public static HostAccommodationsView newInstance(String param1, String param2) {
        HostAccommodationsView fragment = new HostAccommodationsView();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_host_accommodations_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAccommodations();
    }

    private void getAccommodations(){
        Call<ArrayList<Accommodation>> call = ClientUtils.accommodationService.findByHostId(TokenUtils.getId());
        call.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.code() == 200) {
                    populateAccommodationCards(response.body());
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("REZ",t.getMessage() != null?t.getMessage():"error");
            }
        });
    }
    private void populateAccommodationCards(List<Accommodation> accommodations) {
        LinearLayout container = getView().findViewById(R.id.host_accommodations_card_container);
        container.removeAllViews();
        for (Accommodation accommodation : accommodations) {
            AccommodationCard fragment = new AccommodationCard();

            Bundle args = new Bundle();
            args.putSerializable("accommodation", accommodation);
            fragment.setArguments(args);

            // Add the fragment to the reviewsContainer
            getChildFragmentManager().beginTransaction()
                    .add(container.getId(), fragment)
                    .commit();
        }
    }
}