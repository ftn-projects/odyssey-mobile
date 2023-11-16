package com.example.odyssey.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.odyssey.R;
import com.example.odyssey.databinding.ActivityHomeBinding;
import com.example.odyssey.databinding.ActivityHomeDrawerBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeDrawerBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Set<Integer> topLevelDestinations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("(¬‿¬)", "HomeActivity onCreate()");

        binding = ActivityHomeDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawer = binding.activityHomeDrawerLayout;
        navigationView = binding.homeNavView;
        toolbar = binding.activityHome.toolbar;
        // Postavljamo toolbar kao glavnu traku za ovu aktivnost
        setSupportActionBar(toolbar);
        // Dobavljamo referencu na glavnu traku za ovu aktivnost
        actionBar = getSupportActionBar();
        if(actionBar != null){
            // postavlja prikazivanje "strelice prema nazad" (back arrow)
            // kao indikatora navigacije na lijevoj strani Toolbar-a.
            actionBar.setDisplayHomeAsUpEnabled(false);
            // postavlja ikonu koja se prikazuje umjesto strelice prema nazad.
            // U ovom slučaju, postavljena je ikona hamburger iz drawable resursa (ic_hamburger).
            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);
            //vo omogućuje da se klikom na gumb 'home' na Toolbar-u
            // aktivira povratak na prethodni zaslon.
            actionBar.setHomeButtonEnabled(true);
        }

        //  ActionBarDrawerToggle se koristi za povezivanje i upravljanje navigation drawer-om
        //  unutar Android aplikacije. ActionBarDrawerToggle je klasa koja olakšava sinhronizaciju
        //  između navigation drawer-a i ActionBar-a (ili Toolbar-a) te omogućava otvaranje
        //  i zatvaranje navigation drawer-a putem ikone u ActionBar-u ili Toolbar-u.
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // dodajemo navigation drawer-u listener za događaje koji se dese.
        // actionBarDrawerToggle prati promene stanja drawera i reaguje na njih.
        drawer.addDrawerListener(actionBarDrawerToggle);
        // syncState() se koristi kako bi se uskladile ikone (npr. "hamburger" ikona)
        // i stanja između ActionBar-a (ili Toolbar-a) i drawer-a. Ova metoda osigurava
        // da se ikona na ActionBar-u (ili Toolbar-u) pravilno menja u zavisnosti
        // od stanja drawer-a (otvoreno ili zatvoreno).
        actionBarDrawerToggle.syncState();

        topLevelDestinations.add(R.id.home_nav_view);
        // NavigationController se koristi za upravljanje promenama destinacija unutar Android
        // aplikacije korištenjem Android Navigation komponente.
        // Pomoću NavController i OnDestinationChangedListener, prati se promena trenutne
        // destinacije (screen-a/fragmenta) unutar aplikacije.
        navController = Navigation.findNavController(this, R.id.fragment_nav_content);

        MenuItem logoutItem = navigationView.getMenu().findItem(R.id.nav_logout);
        logoutItem.setOnMenuItemClickListener(item -> {
            Toast.makeText(HomeActivity.this, "LOGOUT", Toast.LENGTH_SHORT).show();
            return true;
        });

        // TODO setup toolbar profile link
        // AppBarConfiguration odnosi se na konfiguraciju ActionBar-a (ili Toolbar-a) u Android aplikaciji
        // kako bi se omogućila navigacija koristeći Android Navigation komponentu.
        // Takođe, postavlja se bočni meni (navigation drawer) u skladu sa
        // konfiguracijom akcione trake i navigacije.
        // Svaki ID menija prosleđuje se kao skup ID-ova jer svaki meni treba smatrati odredištima najvišeg nivoa.
        mAppBarConfiguration = new AppBarConfiguration
                .Builder(R.id.nav_home, R.id.nav_profile, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        // Ova linija koda postavlja navigationView da radi zajedno sa NavController-om.
        // To znači da će NavigationView reagovati na korisničke interakcije i navigaciju kroz aplikaciju putem NavController-a.
        NavigationUI.setupWithNavController(navigationView, navController);
        // Ova linija koda povezuje NavController sa ActionBar-om (ili Toolbar-om) tako da ActionBar (ili Toolbar)
        // može pravilno reagovati na navigaciju kroz različite destinacije koje su navedene unutar mAppBarConfiguration.
        // NavController će upravljati povratnom strelicom i ponašanjem akcione trake u skladu sa postavljenim destinacijama.
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menu.clear(); ako imamo u fragmentima menije
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}