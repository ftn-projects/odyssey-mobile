package com.example.odyssey.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.odyssey.BuildConfig;
import com.example.odyssey.R;
import com.example.odyssey.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private String role = "GUEST"; // edit to change role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("(¬‿¬)", "HomeActivity onCreate()");
        Log.d("da",System.getProperty("userToken"));

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupActionBar(binding.activityHomeBase.toolbar, binding.mainDrawerLayout);
        setupNavigation(binding.mainNavView, binding.mainDrawerLayout, getMenuId(role));
    }

    private void setupActionBar(Toolbar toolbar, DrawerLayout drawer) {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void setupNavigation(NavigationView navView, DrawerLayout drawer, int menuId) {
        navView.getMenu().clear();
        navView.inflateMenu(menuId);

        navView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(item -> {
            Toast.makeText(MainActivity.this, "LOGOUT", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        });

        navController = Navigation.findNavController(this, R.id.fragment_container_main);
        appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.nav_home, R.id.nav_guest_reservations, R.id.nav_host_reservations,
                R.id.nav_host_accommodations, R.id.nav_host_stats, R.id.nav_admin_accommodations, R.id.nav_admin_reviews, R.id.nav_admin_users)
                .setOpenableLayout(drawer)
                .build();
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    private int getMenuId(String role) {
        switch (role) {
            case "GUEST":
                return R.menu.drawer_guest_menu;
            case "HOST":
                return R.menu.drawer_host_menu;
            case "ADMIN":
                return R.menu.drawer_admin_menu;
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menu.clear();  if there are menus for specific fragments inside toolbar
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        navController = Navigation.findNavController(this, R.id.fragment_container_main);
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.fragment_container_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}