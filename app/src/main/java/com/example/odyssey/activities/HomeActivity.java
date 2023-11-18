package com.example.odyssey.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.odyssey.R;
import com.example.odyssey.fragments.FilterPopupDialog;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_main_screen_layout);

        ImageButton showPopupButton = findViewById(R.id.filter_button);
        showPopupButton.setOnClickListener(view -> showPopup());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void showPopup() {
        FilterPopupDialog dialog = new FilterPopupDialog();
        dialog.show(getSupportFragmentManager(), "filterPopupDialog");
    }
}