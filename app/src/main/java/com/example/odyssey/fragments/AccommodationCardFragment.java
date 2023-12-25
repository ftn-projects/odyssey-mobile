package com.example.odyssey.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationCardFragment extends LinearLayout {

    private ShapeableImageView imageView;
    private Accommodation accommodation;
    private ImageView heartImageView;
    private TextView locationTextView;
    private AnimatedVectorDrawable emptyHeart;
    private AnimatedVectorDrawable fillHeart;
    private TextView accommodationNameView;
    private TextView accommodationRatingView;

    LinearLayout pricingSection;

    LinearLayout perPricingSection;

    LinearLayout totalPriceSection;

    TextView perPricingPriceNumberView;
    TextView totalPriceNumberView;
    TextView perPricingType;
    private boolean full = false;

    public AccommodationCardFragment(Context context) {
        super(context);
        init();
    }

    public AccommodationCardFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AccommodationCardFragment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
        if(accommodation!=null) {
            setImages();

            if (accommodation.getAddress() != null)
                locationTextView.setText(accommodation.getAddress().getCity() + ", " + accommodation.getAddress().getCountry());

            if (accommodation.getTitle() != null)
                accommodationNameView.setText(accommodation.getTitle());

            if (accommodation.getAverageRating() != null)
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

            }
            else{
                pricingSection.setVisibility(View.GONE);
            }

        }
    }

    public void setImages(){
        Call<ArrayList<String>> call = ClientUtils.accommodationService.getImages(accommodation.getId());
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if(response.code()==200){
                    List<String> accommodationImages = response.body();
                    if(accommodationImages!=null && accommodationImages.size()>0){
                        String imagePath = ClientUtils.SERVICE_API_PATH + "accommodations/" + accommodation.getId() + "/images/" + accommodationImages.get(0);
                        Glide.with(getContext()).load(imagePath).into(imageView);
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

    private void init() {
        // Inflate the layout
        LayoutInflater.from(getContext()).inflate(R.layout.accommodation_card, this, true);

        // Find views
        imageView = findViewById(R.id.cardImageView);
        heartImageView = findViewById(R.id.heartImageView);
        locationTextView = findViewById(R.id.locationTextView);
        accommodationNameView = findViewById(R.id.accommodationTitleTextView);
        pricingSection = findViewById(R.id.cardPricingSection);
        perPricingSection = findViewById(R.id.cardPricePerPricing);
        perPricingPriceNumberView = findViewById(R.id.perPricingPriceNumber);
        totalPriceSection = findViewById(R.id.cardPriceTotalPricing);
        totalPriceNumberView = findViewById(R.id.totalPricePricingNumber);
        perPricingType = findViewById(R.id.perPricingType);


        emptyHeart = (AnimatedVectorDrawable) getContext().getDrawable(R.drawable.avd_heart_empty);
        fillHeart = (AnimatedVectorDrawable) getContext().getDrawable(R.drawable.avd_heart_fill);

        // Set click listener for the heart
        heartImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                animateHeartFill();
            }
        });
    }

    private void animateHeartFill() {
        AnimatedVectorDrawable drawable = full ? emptyHeart : fillHeart;
        heartImageView.setImageDrawable(drawable);
        drawable.start();
        full = !full;

        // Log the click event and animation start
        Log.d("AccommodationCard", "Heart clicked! Animation started: " + (full ? "Empty to Fill" : "Fill to Empty"));
    }
}