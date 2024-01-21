package com.example.odyssey.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    public final int SPLASH_TIME_OUT = 5000;
    public final int WAIT_FOR_ACTION_TIME_OUT = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ClientUtils.setPreferences(getSharedPreferences(TokenUtils.APPLICATION_PREFERENCES_KEY, Context.MODE_PRIVATE));

        if (isConnected()) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    proceed();
                }
            }, SPLASH_TIME_OUT);
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                            "You are not connected to the internet.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("", v -> {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }).show();


            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isConnected()) proceed();
                }
            }, WAIT_FOR_ACTION_TIME_OUT);
        }
    }

    private void proceed() {
        if (isLoggedIn()) {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            Network network = manager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities caps = manager.getNetworkCapabilities(network);
                return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
    }

    private boolean isLoggedIn() {
        return TokenUtils.getToken(getApplicationContext()) != null;
    }
}