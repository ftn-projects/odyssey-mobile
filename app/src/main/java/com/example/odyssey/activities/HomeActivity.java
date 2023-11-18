package com.example.odyssey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.odyssey.R;
import com.example.odyssey.fragments.HomeFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        if (savedInstanceState == null) {
            // Create an instance of HomeFragment
            HomeFragment homeFragment = new HomeFragment();

            // Use FragmentTransaction to add HomeFragment to the activity
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeFragment) // Replace with the ID of your container view
                    .commit();
        }
    }
}