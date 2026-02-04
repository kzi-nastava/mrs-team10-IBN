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

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ubercorp.R;
import com.example.ubercorp.databinding.ActivityHomeBinding;
import com.example.ubercorp.utils.JwtUtils;
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

        topLevelDestinations.add(R.id.notification);
        topLevelDestinations.add(R.id.nav_settings);

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);

        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            Log.i("UberComp", "Destination Changed");
        });

        mAppBarConfiguration = new AppBarConfiguration
                .Builder(R.id.accountFragment, R.id.rating, R.id.ride_history, R.id.tracking_ride, R.id.nav_settings, R.id.notification, R.id.routeFragment, R.id.incomingRideFragment, R.id.trackingRouteFragment)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        updateMenuVisibility();
        setupNavigation();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPref = getSharedPreferences("uber_corp", MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);
        return token != null && !token.isEmpty();
    }

    private void updateMenuVisibility() {
        Menu menu = navigationView.getMenu();
        boolean isLoggedIn = isUserLoggedIn();

        MenuItem accountItem = menu.findItem(R.id.accountFragment);
        MenuItem logoutItem = menu.findItem(R.id.logout);

        if (accountItem != null) {
            accountItem.setVisible(isLoggedIn);
        }
        if (logoutItem != null) {
            logoutItem.setVisible(isLoggedIn);
        }
        if (isUserLoggedIn()){
            updateMenuBasedOnRole(menu);
        }
    }

    private void updateMenuBasedOnRole(Menu menu){
        SharedPreferences sharedPref = getSharedPreferences("uber_corp", MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);
        String role = JwtUtils.getRoleFromToken(token);
        switch(role){
            case "passenger":
                MenuItem incoming = menu.findItem(R.id.incoming_ride);
                incoming.setVisible(false);
                break;
            case "driver":
                MenuItem chat_support = menu.findItem(R.id.rating);
                chat_support.setVisible(false);
                break;
            case "administrator":
                break;
        }
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.logout) {
                showLogoutDialog();
                return true;
            } else if (id == R.id.routeFragment) {
                navController.navigate(R.id.routeFragment);
            } else if (id == R.id.accountFragment) {
                navController.navigate(R.id.accountFragment);
            } else if (id == R.id.rating) {
                navController.navigate(R.id.rating);
            } else if (id == R.id.ride_history) {
                navController.navigate(R.id.ride_history);
            } else if (id == R.id.incoming_ride) {
                navController.navigate(R.id.incomingRideFragment);
            } else if (id == R.id.tracking_ride) {
                navController.navigate(R.id.trackingRouteFragment);
            }

            drawer.closeDrawers();
            return true;
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void performLogout() {
        SharedPreferences sharedPref = getSharedPreferences("uber_corp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMenuVisibility();
    }
}