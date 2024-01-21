package com.example.odyssey.fragments;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationCard extends Fragment {

    private Accommodation accommodation;
    private AnimatedVectorDrawable emptyHeart;
    private AnimatedVectorDrawable fillHeart;

    private boolean full = false;


    public AccommodationCard() {
        // Required empty public constructor
    }

    public static AccommodationCard newInstance(String param1, String param2) {
        AccommodationCard fragment = new AccommodationCard();
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
        accommodation = (Accommodation) getArguments().getSerializable("accommodation");
        View view = inflater.inflate(R.layout.accommodation_card, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (accommodation == null || getView() == null) return;


        TextView locationTextView = getView().findViewById(R.id.locationTextView);
        TextView accommodationNameView = getView().findViewById(R.id.accommodationTitleTextView);
        TextView accommodationRatingView = getView().findViewById(R.id.accommodationRatingTextView);
        TextView perPricingPriceNumberView = getView().findViewById(R.id.perPricingPriceNumber);
        TextView perPricingType = getView().findViewById(R.id.perPricingType);
        TextView totalPriceNumberView = getView().findViewById(R.id.totalPricePricingNumber);
        LinearLayout totalPriceSection = getView().findViewById(R.id.cardPriceTotalPricing);
        LinearLayout pricingSection = getView().findViewById(R.id.cardPricingSection);
        ShapeableImageView imageView = getView().findViewById(R.id.cardImageView);

        emptyHeart = (AnimatedVectorDrawable) getContext().getDrawable(R.drawable.avd_heart_empty);
        fillHeart = (AnimatedVectorDrawable) getContext().getDrawable(R.drawable.avd_heart_fill);
        setImages(imageView);
        ImageView heartImageView = getView().findViewById(R.id.heartImageView);

        heartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkFavorites(true);
            }
        });
        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putSerializable("Accommodation", accommodation);
                Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_details, args);
            }
        });


        locationTextView.setText(accommodation.getAddress().getCity() + ", " + accommodation.getAddress().getCountry());
        accommodationNameView.setText(accommodation.getTitle());
        if(accommodation.getAverageRating()!=null)
            accommodationRatingView.setText(accommodation.getAverageRating().toString());
        if (accommodation.getDefaultPrice() != null && accommodation.getDefaultPrice() > 0) {
            perPricingPriceNumberView.setText(accommodation.getDefaultPrice().toString());
            if (accommodation.getPricing() == Accommodation.PricingType.PER_NIGHT)
                perPricingType.setText("per night");
            else
                perPricingType.setText("per guest");

            if (accommodation.getTotalPrice() != null && accommodation.getTotalPrice() > 0)
                totalPriceNumberView.setText(accommodation.getTotalPrice().toString());
            else
                totalPriceSection.setVisibility(View.GONE);

        } else {
            pricingSection.setVisibility(View.GONE);
        }

        checkFavorites(false);

    }

    private void animateHeartFill() {
        ImageView heartImageView = getView().findViewById(R.id.heartImageView);
        AnimatedVectorDrawable drawable = full ? fillHeart : emptyHeart;
        heartImageView.setImageDrawable(drawable);
        drawable.start();

        Log.d("AccommodationCard", "Heart clicked! Animation started: " + (full ? "Empty to Fill" : "Fill to Empty"));
    }

    public void setImages(ImageView imageView) {
        if (imageView == null) {
            Log.e("AccommodationCard", "ImageView is null");
            return;
        }
        Call<ArrayList<String>> call = ClientUtils.accommodationService.getImages(accommodation.getId());
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if (response.code() == 200) {
                    List<String> accommodationImages = response.body();
                    if (accommodationImages != null && accommodationImages.size() > 0) {
                        String imagePath = ClientUtils.SERVICE_API_PATH + "accommodations/" + accommodation.getId() + "/images/" + accommodationImages.get(0);
                        Glide.with(getContext()).load(imagePath).into(imageView);
                    }
                } else {
                    String error = ClientUtils.getError(response, "Error while getting images");
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
            }
        });

    }

    private void checkFavorites(Boolean executable) {
        if(TokenUtils.getId(requireContext())==null || !TokenUtils.getRole(requireContext()).equals("GUEST")) {
            if(executable){
                Toast.makeText(getContext(), "You must be logged in as a guest to favorite accommodations", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        Call<ArrayList<Accommodation>> call = ClientUtils.accommodationService.getFavorites(TokenUtils.getId(requireContext()));
        call.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.code() == 200) {
                    ArrayList<Accommodation> accommodationList = response.body();
                    boolean idExists = accommodationList.stream().anyMatch(a -> a.getId().equals(accommodation.getId()));
                    if(idExists){
                        full = true;
                        animateHeartFill();
                        if(executable){
                            removeFavorite();
                        }
                    }
                    else{
                        full = false;
                        animateHeartFill();
                        if(executable){
                            addFavorite();
                        }
                    }

                } else {
                    String error = ClientUtils.getError(response, "Error while getting favorite accommodations");
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }


    private void addFavorite(){
        Call<Void> call = ClientUtils.accommodationService.addFavorite(TokenUtils.getId(requireContext()), accommodation.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200){
                    Log.d("REZ", "Success");
                    Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                    full = true;
                    animateHeartFill();
                }
                else {
                    String error = ClientUtils.getError(response, "Error while adding to favorites");
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void removeFavorite(){
        Call<Void> call = ClientUtils.accommodationService.removeFavorite(TokenUtils.getId(requireContext()), accommodation.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code()==200){
                    Log.d("REZ", "Success");
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                    full = false;
                    animateHeartFill();
                }
                else {
                    String error = ClientUtils.getError(response, "Error while removing from favorites");
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }
}