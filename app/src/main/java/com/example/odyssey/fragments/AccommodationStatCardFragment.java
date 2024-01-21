package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.stats.AccommodationTotalStats;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationStatCardFragment extends Fragment {

    private AccommodationTotalStats accommodationStats;

    private String imagePath;

    private void setImagePath(String imagePath){
        this.imagePath = imagePath;
        ShapeableImageView imageView = getView().findViewById(R.id.statCardImageView);
        Glide.with(getContext()).load(imagePath).into(imageView);
        Log.d("REZ",imagePath);
    }
    public static AccommodationStatCardFragment newInstance(String param1, String param2) {
        AccommodationStatCardFragment fragment = new AccommodationStatCardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AccommodationStatCardFragment() {
        // Required empty public constructor
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
        accommodationStats = (AccommodationTotalStats) getArguments().getSerializable("stats");
        View view = inflater.inflate(R.layout.fragment_accommodation_stat_card, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadImage();
        populateData();
        LinearLayout container = getView().findViewById(R.id.accommodation_stat_card_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccommodationStatDialogFragment dialogFragment = AccommodationStatDialogFragment.newInstance(accommodationStats);
                dialogFragment.show(getParentFragmentManager(),"accommodationStatDialog");
            }
        });
    }

    private void populateData(){
        if(accommodationStats==null) return;

        TextView accommodationNameView = getView().findViewById(R.id.stat_card_title_text_view);
        accommodationNameView.setText(accommodationStats.getAccommodation().getTitle());

        TextView accommodationRatingView = getView().findViewById(R.id.stat_card_reservations_text_view);
        accommodationRatingView.setText("Total reservations: " +accommodationStats.getTotalReservations().toString());

        TextView perPricingPriceNumberView = getView().findViewById(R.id.stat_card_income_text_view);
        perPricingPriceNumberView.setText("Total income: " + accommodationStats.getTotalIncome().toString());
    }

    private void loadImage(){
        Call<ArrayList<String>> call = ClientUtils.accommodationService.getImages(accommodationStats.getAccommodation().getId());
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if(response.code()==200){
                    List<String> accommodationImages = response.body();
                    if(accommodationImages!=null && accommodationImages.size()>0){
                        setImagePath(ClientUtils.SERVICE_API_PATH + "accommodations/" + accommodationStats.getAccommodation().getId() + "/images/" + accommodationImages.get(0));
                    }
                }else{
                    Log.d("REZ","Bad");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Log.d("REZ",t.getMessage() != null?t.getMessage():"error");
            }
        });

    }
}