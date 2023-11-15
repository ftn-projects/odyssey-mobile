package com.example.odyssey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.odyssey.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        /*
         * Log klasa se koristi za logovanje informacija, errora, warning-a unutar aplikacije.
         * Logovi se ispisuju u logcat delu i moguce ih je filtrirati po zadatom tag-u
         * (prvi parametar)
         * */
        Log.d("Odyssey", "HomeActivity onCreate()");
    }
}