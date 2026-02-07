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
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.DriverService;
import com.example.ubercorp.api.FcmService;
import com.example.ubercorp.databinding.ActivityHomeBinding;
import com.example.ubercorp.dto.AppNotificationDTO;
import com.example.ubercorp.dto.IncomingRideDTO;
import com.example.ubercorp.managers.MyNotificationManager;
import com.example.ubercorp.utils.JwtUtils;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

import androidx.core.app.NotificationCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements MyNotificationManager.NotificationListener {

    private static final String TAG = "HomeActivity";
    private static final String CHANNEL_ID = "uber_fcm_notifications";
    private static final String PANIC_ID = "uber_fcm_notifications_panic";
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

    private Set<Long> shownNotificationIds = new HashSet<>();

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
        setupNotifs();

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();

            if (extras.getBoolean("open_notification_fragment", false)) {
                navController.navigate(R.id.notification);
            } else if (extras.containsKey("ride_id")) {
                navController.navigate(R.id.incomingRideFragment, extras);
            }
        }
    }

    public void setupNotifs() {
        if (isUserLoggedIn()) {
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

        //notificationManager.setListener(this);

        String wsUrl = BuildConfig.API_HOST + "socket";
        Log.d(TAG, "Connecting to WebSocket (send-only mode): " + wsUrl);

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
        MenuItem loginItem = menu.findItem(R.id.login);
        MenuItem logoutItem = menu.findItem(R.id.logout);
        MenuItem rideHistoryItem = menu.findItem(R.id.ride_history);

        if (accountItem != null) {
            accountItem.setVisible(isLoggedIn);
        }

        if (loginItem != null) {
            loginItem.setVisible(!isLoggedIn);
        }
        if (logoutItem != null) {
            logoutItem.setVisible(isLoggedIn);
        }

        if (rideHistoryItem != null) {
            rideHistoryItem.setVisible(isLoggedIn);
        }

        MenuItem incoming = menu.findItem(R.id.incoming_ride);
        if (incoming != null) {
            incoming.setVisible(false);
        }
        MenuItem tracking = menu.findItem(R.id.tracking_ride);
        if (tracking != null) {
            tracking.setVisible(false);
        }

        if (isLoggedIn) {
            topLevelDestinations.add(R.id.notification);
            updateMenuBasedOnRole(menu);
        }

        MenuItem chat_support = menu.findItem(R.id.rating);
        if (chat_support != null) {
            chat_support.setVisible(false);
        }
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.login) {
                navigateToLogin();
                return true;
            } else if (id == R.id.logout) {
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

    private void navigateToLogin() {
        drawer.closeDrawers();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void updateMenuBasedOnRole(Menu menu){
        SharedPreferences sharedPref = getSharedPreferences("uber_corp", MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);
        String role = JwtUtils.getRoleFromToken(token);
        MenuItem tracking = menu.findItem(R.id.tracking_ride);
        switch(role){
            case "passenger":
                if (tracking != null) {
                    tracking.setVisible(true);
                }
                break;
            case "driver":
                MenuItem incoming = menu.findItem(R.id.incoming_ride);
                if (incoming != null) {
                    incoming.setVisible(true);
                }
                MenuItem chat_support = menu.findItem(R.id.rating);
                if (chat_support != null) {
                    chat_support.setVisible(false);
                }
                if (tracking != null) {
                    tracking.setVisible(true);
                }
                break;
            case "administrator":
                break;
        }
    }

    @Override
    public void onNotificationReceived(AppNotificationDTO notification) {
//        Log.d(TAG, "Notification received: " + notification.getTitle());
//        if (shownNotificationIds.contains(notification.getId())) {
//            Log.d(TAG, "Notification already shown, skipping: " + notification.getId());
//            return;
//        }
//
//        shownNotificationIds.add(notification.getId());
//
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("open_notification_fragment", true);
//
//        showSystemNotification(
//                notification.getTitle(),
//                notification.getContent(),
//                false,
//                bundle,
//                notification.getId()
//        );
    }

    @Override
    public void onRideReceived(IncomingRideDTO ride) {
        Log.d(TAG, "Incoming ride received: " + ride.getId());

        long notificationId = ride.getId() + 1000000;
        if (shownNotificationIds.contains(notificationId)) {
            Log.d(TAG, "Ride notification already shown, skipping: " + notificationId);
            return;
        }

        shownNotificationIds.add(notificationId);

        Bundle bundle = new Bundle();
        bundle.putLong("ride_id", ride.getId());
        bundle.putString("pickup", ride.getRoute().getStations().get(0).getAddress());
        bundle.putString("dropoff",
                ride.getRoute().getStations()
                        .get(ride.getRoute().getStations().size() - 1)
                        .getAddress());

        showSystemNotification(
                "New Ride Request",
                "Pickup: " + ride.getRoute().getStations().get(0).getAddress(),
                true,
                bundle,
                notificationId
        );
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

    private void showSystemNotification(String title, String content, boolean isHighPriority, Bundle bundle, Long notificationId) {
        String channelId = CHANNEL_ID;

        if (title != null && title.equals("PANIC")) {
            channelId = PANIC_ID;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(isHighPriority ?
                        NotificationCompat.PRIORITY_MAX :
                        NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 250, 500})
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setNumber(1);

        if (isHighPriority) {
            builder.setCategory(NotificationCompat.CATEGORY_CALL);
        }

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this,
                notificationId.intValue(),
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );

        builder.setContentIntent(pendingIntent);

        if (systemNotificationManager != null) {
            systemNotificationManager.notify(notificationId.intValue(), builder.build());
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

        shownNotificationIds.clear();

        SharedPreferences sharedPref = getSharedPreferences("uber_corp", MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);
        String role = JwtUtils.getRoleFromToken(token);

        deleteFcmToken(token);

        if ("driver".equals(role)) {
            ApiClient.getInstance().createService(DriverService.class).toggleDriverStatus("Bearer " + token, false)
                    .enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                            logoutCleanup();
                        }

                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                            Log.e(TAG, "Failed to toggle driver status: " + t.getMessage());
                            logoutCleanup();
                        }
                    });
        } else {
            logoutCleanup();
        }
    }

    private void deleteFcmToken(String authToken) {
        if (authToken == null) {
            Log.e(TAG, "Auth token is NULL, cannot delete FCM token");
            return;
        }

        FcmService fcmService = ApiClient.getInstance().createService(FcmService.class);
        Call<Void> call = fcmService.deleteFcmToken("Bearer " + authToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "FCM token deleted successfully");
                } else {
                    Log.e(TAG, "Failed to delete FCM token - Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Failed to delete FCM token", t);
            }
        });
    }

    private void logoutCleanup() {
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

        boolean isLoggedIn = isUserLoggedIn();
        MenuItem notificationItem = menu.findItem(R.id.notification);
        if (notificationItem != null) {
            notificationItem.setVisible(isLoggedIn);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.notification) {
            if (isUserLoggedIn()) {
                navController.navigate(R.id.notification);
                return true;
            } else {
                Toast.makeText(this, "Please login to view notifications", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMenuVisibility();

        invalidateOptionsMenu();

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
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (notificationManager != null) {
            notificationManager.disconnect();
        }
    }
}