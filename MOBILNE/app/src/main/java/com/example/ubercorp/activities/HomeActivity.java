package com.example.ubercorp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ubercorp.R;
import com.example.ubercorp.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;


public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
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
        Log.i("UberComp", "HomeActivity onCreate()");

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        toolbar = binding.activityHomeBase.toolbar;

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);
            //actionBar.setHomeButtonEnabled(false);
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        topLevelDestinations.add(R.id.notification);
        topLevelDestinations.add(R.id.nav_settings);

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            Log.i("UberComp", "Destination Changed");

            int id = navDestination.getId();
            boolean isTopLevelDestination = topLevelDestinations.contains(id);

            if (!isTopLevelDestination) {
                if (id == R.id.blank_fragment) {

                } else if (id == R.id.my_account) {
                } else if (id == R.id.chat) {
                } else if (id == R.id.ride_history) {
                }
                drawer.closeDrawers();
            } else {
                if (id == R.id.nav_settings) {
                    Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.notification) {
                    Toast.makeText(HomeActivity.this, "Notification", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAppBarConfiguration = new AppBarConfiguration
                .Builder(R.id.my_account, R.id.chat, R.id.ride_history, R.id.tracking_ride, R.id.nav_settings, R.id.notification)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupWithNavController(navigationView, navController);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

    }

    /*
     * Android aktivnosti koja se koristi za inflaciju (kreiranje) menija u aplikaciji.
     * Ova metoda je dio životnog ciklusa aktivnosti i poziva se kada se izrađuje akcijska traka (action bar)
     * za prikazivanje opcija u meniju (menu items) za određenu aktivnost.
     * Ako zelimo da nasa aktivnost/fragment prikaze ikonice unutar toolbar-a
     * to se odvija u nekoliko koraka:
     * 1. Potrebno je da napravimo listu koja specificira koje sve ikonice imamo unutar menija,
     * koje ikone koristimo i da li se uvek prikazuju ili ne.
     * 2. Nakon toga koristeci metodu onCreateOptionsMenu postavljamo ikone na toolbar.
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menu.clear();
        // koristimo ako je nasa arhitekrura takva da imamo jednu aktivnost
        // i vise fragmentaa gde svaki od njih ima svoj menu unutar toolbar-a

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /*
     * Prestavlja deo aktivnosti unutar Android aplikacije koji se koristi
     * za upravljanje akcijama koje se odvijaju kada korisnik odabere opciju
     * izbornika (menu item) u ActionBar-u (alatnoj traci) ili Toolbar-u aplikacije.
     * Da bi znali na koju ikonicu je korisnik kliknuo
     * treba da iskoristimo jedinstveni identifikator za svaki element u listi.
     * Metoda onOptionsItemSelected ce vratiti MenuItem na koji je korisnik kliknuo.
     * Ako znamo da ce on vratiti i id, tacno znamo na koji element je korisnik
     * kliknuo, i shodno tome reagujemo.
     * */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.nav_settings:
//                Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_language:
//                Toast.makeText(HomeActivity.this, "Language", Toast.LENGTH_SHORT).show();
//                break;
//        }
        // U ovoj metodi, prvo se pomoću Navigation komponente pronalazi NavController.
        // NavController je odgovoran za upravljanje navigacijom unutar aplikacije
        // koristeći Androidov servis za navigaciju.
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        // Nakon toga, koristi se NavigationUI.onNavDestinationSelected(item, navController)
        // kako bi se omogućila integracija između MenuItem-a i odredišta unutar aplikacije
        // definisanih unutar navigacionog grafa (NavGraph).
        // Ova funkcija proverava da li je odabrana stavka izbornika povezana s nekim
        // odredištem unutar navigacionog grafa i pokreće tu navigaciju ako postoji
        // odgovarajuće podudaranje.
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    // Kada korisnik pritisne "back" ili navigacijsku strelicu unutar aplikacije,
    // ova metoda se poziva kako bi odredila ponašanje povratka.
    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}