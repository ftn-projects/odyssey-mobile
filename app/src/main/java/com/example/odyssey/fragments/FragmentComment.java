package com.example.odyssey.fragments;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.odyssey.R;
import com.google.android.material.imageview.ShapeableImageView;

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
        LayoutInflater.from(getContext()).inflate(R.layout.comment_card_layout, this, true);
    }
}
