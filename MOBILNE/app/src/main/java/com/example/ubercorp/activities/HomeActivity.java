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
import android.app.NotificationChannel;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ubercorp.BuildConfig;
import com.example.ubercorp.R;
import com.example.ubercorp.databinding.ActivityHomeBinding;
import com.example.ubercorp.dto.AppNotificationDTO;
import com.example.ubercorp.dto.IncomingRideDTO;
import com.example.ubercorp.managers.MyNotificationManager;
import com.example.ubercorp.utils.JwtUtils;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

import androidx.core.app.NotificationCompat;

public class HomeActivity extends AppCompatActivity
        implements MyNotificationManager.NotificationListener {

    private static final String TAG = "HomeActivity";
    private static final String CHANNEL_ID = "uber_notifications";
    private static final String PANIC_ID = "uber_notifications_panic";
    private ActivityHomeBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Set<Integer> topLevelDestinations = new HashSet<>();

    private MyNotificationManager notificationManager;
    private android.app.NotificationManager systemNotificationManager;

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

        createNotificationChannel();
        setupNotificationManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Uber Notifications",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for rides and updates");
            channel.enableVibration(true);
            channel.setShowBadge(true);

            Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.youve_been_informed_345);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            channel.setSound(sound, audioAttributes);

            NotificationChannel panic = new NotificationChannel(
                    PANIC_ID,
                    "Uber Panic Notifications",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            panic.setDescription("Notifications for emergencies");
            panic.enableVibration(true);
            panic.setShowBadge(true);

            Uri panicSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.attention_required_127);
            panic.setSound(panicSound, audioAttributes);

            systemNotificationManager = getSystemService(android.app.NotificationManager.class);
            systemNotificationManager.createNotificationChannel(channel);
            systemNotificationManager.createNotificationChannel(panic);
        } else {
            systemNotificationManager = (android.app.NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
        }
    }

    private void setupNotificationManager() {
        if (!isUserLoggedIn()) {
            Log.d(TAG, "User not logged in, skipping WebSocket connection");
            return;
        }

        notificationManager = MyNotificationManager.getInstance(this);
        notificationManager.setListener(this);

        String wsUrl = BuildConfig.API_HOST + "socket";
        Log.d(TAG, "Connecting to WebSocket: " + wsUrl);
        SharedPreferences sharedPref = getSharedPreferences("uber_corp", MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);
        String email = JwtUtils.getEmailFromToken(token);
        notificationManager.connect(wsUrl, token, email);
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
                if (incoming != null) {
                    incoming.setVisible(false);
                }
                break;
            case "driver":
                MenuItem chat_support = menu.findItem(R.id.rating);
                if (chat_support != null) {
                    chat_support.setVisible(false);
                }
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
            } else if (id == R.id.notification) {
                navController.navigate(R.id.notification);
            }

            drawer.closeDrawers();
            return true;
        });
    }

    @Override
    public void onNotificationReceived(AppNotificationDTO notification) {
        Log.d(TAG, "Notification received: " + notification.getTitle());

        showSystemNotification(
                notification.getTitle(),
                notification.getContent(),
                false
        );

        runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) return;
            Toast.makeText(getApplicationContext(),
                    notification.getTitle(),
                    Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onRideReceived(IncomingRideDTO ride) {
        Log.d(TAG, "Incoming ride received: " + ride.getId());

        showSystemNotification(
                "New Ride Request",
                "Pickup: " + ride.getRoute().getStations().get(0).getAddress(),
                true
        );

        runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) return;
            if (navController.getCurrentDestination() == null) return;

            int currentId = navController.getCurrentDestination().getId();
            if (currentId == R.id.incomingRideFragment) return;

            Bundle bundle = new Bundle();
            bundle.putLong("ride_id", ride.getId());
            bundle.putString("pickup", ride.getRoute().getStations().get(0).getAddress());
            bundle.putString("dropoff",
                    ride.getRoute().getStations()
                            .get(ride.getRoute().getStations().size() - 1)
                            .getAddress());

            navController.navigate(R.id.incomingRideFragment, bundle);
        });

    }

    @Override
    public void onConnectionStatusChanged(boolean isConnected) {
        Log.d(TAG, "WebSocket connection status: " + isConnected);

        runOnUiThread(() -> {
            String message = isConnected ? "Connected to server" : "Disconnected from server";
            Log.i(TAG, message);
        });
    }

    @Override
    public void onError(String error) {
        Log.e(TAG, "Notification error: " + error);

        runOnUiThread(() -> {
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
        });
    }

    private void showSystemNotification(String title, String content, boolean isHighPriority) {
        String channelId = CHANNEL_ID;
        if (title.equals("PANIC")) channelId = PANIC_ID;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(isHighPriority ?
                        NotificationCompat.PRIORITY_MAX :
                        NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 250, 500});

        if (isHighPriority) {
            //builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
            builder.setCategory(NotificationCompat.CATEGORY_CALL);
        }

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this,
                0,
                intent,
                android.app.PendingIntent.FLAG_IMMUTABLE
        );

        builder.setContentIntent(pendingIntent);

        if (systemNotificationManager != null) {
            systemNotificationManager.notify(
                    (int) System.currentTimeMillis(),
                    builder.build()
            );
        }
    }

    private void showLogoutDialog() {
        drawer.closeDrawers();

        drawer.post(() -> {
            if (isFinishing() || isDestroyed()) return;
            if (!drawer.isAttachedToWindow()) return;

            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> performLogout())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }


    private void performLogout() {
        if (notificationManager != null) {
            notificationManager.disconnect();
        }

        SharedPreferences sharedPref = getSharedPreferences("uber_corp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

        if (!isFinishing()
                && !isDestroyed()
                && isUserLoggedIn()
                && notificationManager != null
                && !notificationManager.isConnected()) {

            SharedPreferences sharedPref = getSharedPreferences("uber_corp", MODE_PRIVATE);
            String token = sharedPref.getString("auth_token", null);
            String email = JwtUtils.getEmailFromToken(token);
            notificationManager.connect(BuildConfig.API_HOST + "socket", token, email);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (notificationManager != null) {
            notificationManager.disconnect();
        }
    }
}