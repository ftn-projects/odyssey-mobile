package com.example.odyssey.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.clients.StompClient;
import com.example.odyssey.databinding.ActivityMainBinding;
import com.example.odyssey.model.notifications.Notification;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private final String role = TokenUtils.getRole(); // edit to change role
    private Toolbar toolbar;
    private BadgeDrawable notificationBadge = null;
    private StompClient stompClient = null;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("(¬‿¬)", "HomeActivity onCreate()");

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        toolbar = binding.activityHomeBase.toolbar;
        setContentView(binding.getRoot());

        setupActionBar();
        setupNavigation(binding.mainNavView, binding.mainDrawerLayout, getMenuId(role));
    }

    private void setupActionBar() {
        toolbar.setPadding(toolbar.getPaddingLeft(), toolbar.getPaddingTop(), 30, toolbar.getPaddingBottom());
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void restart() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void setupNavigation(NavigationView navView, DrawerLayout drawer, int menuId) {
        navView.getMenu().clear();
        navView.inflateMenu(menuId);

        if (role != null) {
            navView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(item -> {
                TokenUtils.removeToken();
                restart();
                return true;
            });
        } else {
            navView.getMenu().findItem(R.id.nav_login).setOnMenuItemClickListener(item -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            });
        }

        navController = Navigation.findNavController(this, R.id.fragment_container_main);
        appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.nav_home, R.id.nav_guest_reservations, R.id.nav_guest_reviews, R.id.nav_host_reservations,
                R.id.nav_host_accommodations, R.id.nav_host_stats, R.id.nav_admin_accommodations, R.id.nav_admin_reviews, R.id.nav_admin_users)
                .setOpenableLayout(drawer)
                .build();
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    private int getMenuId(String role) {
        if (role == null) return R.menu.drawer_unauth_menu;

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
        if (role != null) {
            getMenuInflater().inflate(R.menu.home_auth_menu, menu);

            MenuItem item = menu.findItem(R.id.nav_notifications);
            removeNotificationBadge();
            createNotificationBadge();

            updateNotificationCount();
            setupStompClient();
        }
        return true;
    }

    @OptIn(markerClass = ExperimentalBadgeUtils.class)
    private void removeNotificationBadge() {
        if (notificationBadge != null) {
            BadgeUtils.detachBadgeDrawable(notificationBadge, toolbar, R.id.nav_notifications);
            notificationBadge = null;
        }
    }

    @OptIn(markerClass = ExperimentalBadgeUtils.class)
    private void createNotificationBadge() {
        notificationBadge = BadgeDrawable.create(this);
        notificationBadge.setBackgroundColor(getResources().getColor(R.color.md_theme_light_onErrorContainer, getTheme()));
        notificationBadge.setBadgeTextColor(getResources().getColor(R.color.md_theme_light_onPrimary, getTheme()));
        notificationBadge.setHorizontalOffset(10);
        notificationBadge.setVerticalOffset(10);
        BadgeUtils.attachBadgeDrawable(notificationBadge, toolbar, R.id.nav_notifications);
    }

    private void setupStompClient() {
        if (stompClient != null) return;

        stompClient = new StompClient();
        stompClient.subscribe("/topic/notifications", this::updateNotificationCount);
    }

    private void updateNotificationCount() {
        ClientUtils.notificationService.findByUserId(TokenUtils.getId(), null, false).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notification>> call, @NonNull Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    List<Notification> notifications = response.body();
                    if (notifications != null) {
                        if (notifications.size() != 0) {
                            if (notificationBadge == null)
                                createNotificationBadge();
                            notificationBadge.setNumber(notifications.size());
                        } else removeNotificationBadge();
                    }
                } else {
                    Log.e("(¬‿¬)", "updateNotificaitonCount(): " + response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notification>> call, @NonNull Throwable t) {
                Log.e("(¬‿¬)", t.getMessage() == null ? "Failed getting unread notifications" : t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (stompClient != null) {
            stompClient.disconnect();
            stompClient = null;
        }
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

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}