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

import com.example.odyssey.R;
import com.google.android.material.imageview.ShapeableImageView;

public class AccommodationCard extends LinearLayout {

    private ShapeableImageView imageView;
    private ImageView heartImageView;
    private AnimatedVectorDrawable emptyHeart;
    private AnimatedVectorDrawable fillHeart;
    private boolean full = false;

    public AccommodationCard(Context context) {
        super(context);
        init();
    }

    public AccommodationCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AccommodationCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Inflate the layout
        LayoutInflater.from(getContext()).inflate(R.layout.accommodation_card, this, true);

        // Find views
        imageView = findViewById(R.id.imageView);
        heartImageView = findViewById(R.id.heartImageView);

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