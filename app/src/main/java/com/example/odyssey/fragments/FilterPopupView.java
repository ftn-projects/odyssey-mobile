package com.example.odyssey.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.odyssey.R;

public class FilterPopupView extends ConstraintLayout {

    public FilterPopupView(Context context) {
        super(context);
        init();
    }

    public FilterPopupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FilterPopupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Inflate the layout
        LayoutInflater.from(getContext()).inflate(R.layout.popup_section, this, true);
    }
}
