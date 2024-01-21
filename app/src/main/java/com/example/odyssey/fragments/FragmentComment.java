package com.example.odyssey.fragments;

import android.content.Context;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.odyssey.R;

public class FragmentComment extends LinearLayout {


    public FragmentComment(Context context) {
        super(context);
        init();
    }

    public FragmentComment(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FragmentComment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Inflate the layout
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_accommodation_review_card, this, true);
    }
}
