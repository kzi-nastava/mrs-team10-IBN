package com.example.ubercorp.fragments;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ubercorp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class AccountFragment extends Fragment {
    // Header & Profile
    private FrameLayout headerBackground, changePassword;
    private LinearLayout userInfoSection;
    private FloatingActionButton fabEditProfile;
    private TextView tvUserName, tvUserEmail;

    // Cards
    private MaterialCardView editFormCard, menuCard, editVehicleCard;

    // Profile Edit
    private TextInputEditText etFirstName, etLastName, etEmail;
    private MaterialButton btnCancel, btnSaveChanges, btnSendChanges, btnSavePassword, btnCancelPassword;

    // Vehicle Edit
    private RadioGroup rgVehicleType;
    private TextInputEditText etNumberOfSeats;
    private MaterialCheckBox cbBabyTransport, cbPetTransport;
    private MaterialButton btnCancelVehicle, btnSaveVehicle;

    // Driver Progress
    private LinearLayout drivingHoursSection;
    private ProgressBar driverProgress;
    private TextView tvDrivingHoursProgress;

    // Menu Items
    private LinearLayout menuPlatformStats, menuRequests, menuManageUsers,
            menuChangePassword, menuDeleteAccount, menuFavorites,
            menuUserStat, menuDriverStat, menuVehicle;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        setupMenuItems(view);
        initializeViews(view);
        setupListeners();
        configureMenuForRole("admin");
        startFlyingTaxiAnimation();

        return view;
    }

    private void initializeViews(View view) {
        // Header & Profile
        headerBackground = view.findViewById(R.id.headerBackground);
        userInfoSection = view.findViewById(R.id.userInfoSection);
        fabEditProfile = view.findViewById(R.id.fabEditProfile);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);

        // Cards
        editFormCard = view.findViewById(R.id.editFormCard);
        menuCard = view.findViewById(R.id.menuCard);
        editVehicleCard = view.findViewById(R.id.editVehicleCard);
        changePassword = view.findViewById(R.id.changePassword);

        // Profile Edit
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnSendChanges = view.findViewById(R.id.btnSendChanges);
        btnSavePassword = view.findViewById(R.id.btnSavePassword);
        btnCancelPassword = view.findViewById(R.id.btnCancelPassword);

        // Vehicle Edit
        rgVehicleType = view.findViewById(R.id.rgVehicleType);
        etNumberOfSeats = view.findViewById(R.id.etNumberOfSeats);
        cbBabyTransport = view.findViewById(R.id.cbBabyTransport);
        cbPetTransport = view.findViewById(R.id.cbPetTransport);
        btnCancelVehicle = view.findViewById(R.id.btnCancelVehicle);
        btnSaveVehicle = view.findViewById(R.id.btnSaveVehicle);

        // Driver Progress
        drivingHoursSection = view.findViewById(R.id.drivingHoursSection);
        driverProgress = view.findViewById(R.id.progressDrivingHours);
        tvDrivingHoursProgress = view.findViewById(R.id.tvDrivingHoursProgress);

        // Menu Items
        menuPlatformStats = view.findViewById(R.id.menuPlatformStats);
        menuRequests = view.findViewById(R.id.menuRequests);
        menuManageUsers = view.findViewById(R.id.menuManageUsers);
        menuChangePassword = view.findViewById(R.id.menuChangePassword);
        menuDeleteAccount = view.findViewById(R.id.menuDeleteAccount);
        menuFavorites = view.findViewById(R.id.menuFavorites);
        menuUserStat = view.findViewById(R.id.menuMyStatistics);
        menuDriverStat = view.findViewById(R.id.menuDriverStatistics);
        menuVehicle = view.findViewById(R.id.menuVehicle);
    }

    private void setupListeners() {
        // Profile Edit
        fabEditProfile.setOnClickListener(v -> showEditProfile());
        btnCancel.setOnClickListener(v -> hideEditProfile());
        btnSaveChanges.setOnClickListener(v -> saveProfileChanges());
        btnSendChanges.setOnClickListener(v -> sendProfileChanges());
        btnSavePassword.setOnClickListener(v -> savePassword());
        btnCancelPassword.setOnClickListener(v -> hideChangePassword());

        // Vehicle Edit
        menuVehicle.setOnClickListener(v -> showEditVehicle());
        btnCancelVehicle.setOnClickListener(v -> hideEditVehicle());
        btnSaveVehicle.setOnClickListener(v -> saveVehicleChanges());

        // Navigate to Change Requests
        menuRequests.setOnClickListener(v -> navigateToChangeRequests());

        menuChangePassword.setOnClickListener(v -> showChangePassword());
        menuManageUsers.setOnClickListener(v -> navigateToManageUsers());
    }

    private void setupMenuItems(View view) {
        setupMenuItem(view.findViewById(R.id.menuPlatformStats), "📊",
                "Platform Statistics", "Get reports", true);
        setupMenuItem(view.findViewById(R.id.menuRequests), "📥",
                "Requests", "Manage change requests", true);
        setupMenuItem(view.findViewById(R.id.menuManageUsers), "👥",
                "Manage Users", "Add or remove users", true);
        setupMenuItem(view.findViewById(R.id.menuChangePassword), "🔑",
                "Change Password", "Update your password", true);
        setupMenuItem(view.findViewById(R.id.menuDeleteAccount), "🗑️",
                "Delete Account", "Permanently delete your account", false);
        setupMenuItem(view.findViewById(R.id.menuFavorites), "⭐",
                "Favorites", "Manage favorite routes", true);
        setupMenuItem(view.findViewById(R.id.menuMyStatistics), "📊",
                "My Statistics", "Get reports", true);
        setupMenuItem(view.findViewById(R.id.menuDriverStatistics), "📊",
                "Driver Statistics", "Get reports", true);
        setupMenuItem(view.findViewById(R.id.menuVehicle), "🚗",
                "My Vehicle", "Manage your vehicle", true);
    }
    private void navigateToChangeRequests() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_account_to_changeRequests);
    }

    private void navigateToManageUsers() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_account_to_manageUsers);
    }
    private void configureMenuForRole(String userRole) {
        hideAllMenuItems();

        switch (userRole) {
            case "admin":
                showViews(menuPlatformStats, menuRequests, menuManageUsers,
                        menuChangePassword, menuDeleteAccount);
                break;

            case "driver":
                showViews(menuVehicle, menuDriverStat, menuChangePassword, menuDeleteAccount);
                tvUserEmail.setVisibility(View.GONE);
                drivingHoursSection.setVisibility(View.VISIBLE);
                updateDriverHours(5.5f);
                btnSaveChanges.setVisibility(View.GONE);
                btnSendChanges.setVisibility(View.VISIBLE);
                break;

            case "user":
                showViews(menuFavorites, menuUserStat, menuChangePassword, menuDeleteAccount);
                break;
        }
    }

    private void hideAllMenuItems() {
        hideViews(menuPlatformStats, menuRequests, menuManageUsers, menuChangePassword,
                menuDeleteAccount, menuFavorites, menuUserStat, menuVehicle, menuDriverStat,
                drivingHoursSection);
        btnSendChanges.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
    }

    private void showViews(View... views) {
        for (View view : views) view.setVisibility(View.VISIBLE);
    }

    private void hideViews(View... views) {
        for (View view : views) view.setVisibility(View.GONE);
    }

    // Edit Profile
    private void showEditProfile() {
        hideViews(userInfoSection, menuCard, tvDrivingHoursProgress, driverProgress);
        showViews(editFormCard);
    }

    private void showChangePassword() {
        changePassword.setVisibility(View.VISIBLE);
        hideViews(userInfoSection, menuCard, tvDrivingHoursProgress, driverProgress);
    }

    private void hideChangePassword() {
        changePassword.setVisibility(View.GONE);
        showViews(userInfoSection, menuCard, tvDrivingHoursProgress, driverProgress);
    }

    private void savePassword() {
        hideChangePassword();
    }

    private void hideEditProfile() {
        hideViews(editFormCard);
        showViews(userInfoSection, menuCard, tvDrivingHoursProgress, driverProgress);
    }

    private void saveProfileChanges() {
        String fullName = etFirstName.getText() + " " + etLastName.getText();
        tvUserName.setText(fullName);
        tvUserEmail.setText(etEmail.getText());
        hideEditProfile();
    }
    private void sendProfileChanges() {
        hideEditProfile();
    }

    // Edit Vehicle
    private void showEditVehicle() {
        hideViews(userInfoSection, menuCard, driverProgress, tvDrivingHoursProgress);
        showViews(editVehicleCard);
    }

    private void hideEditVehicle() {
        hideViews(editVehicleCard);
        showViews(userInfoSection, menuCard, driverProgress, tvDrivingHoursProgress);
    }

    private void saveVehicleChanges() {
        int selectedId = rgVehicleType.getCheckedRadioButtonId();
        String vehicleType = selectedId == R.id.rbStandard ? "Standard" :
                selectedId == R.id.rbLuxury ? "Luxury" : "Van";

        String seats = etNumberOfSeats.getText().toString();
        boolean babyTransport = cbBabyTransport.isChecked();
        boolean petTransport = cbPetTransport.isChecked();

        hideEditVehicle();
    }

    private void updateDriverHours(float hoursWorked) {
        driverProgress.setMax(8);
        driverProgress.setProgress((int) hoursWorked);
        tvDrivingHoursProgress.setText(String.format("%.1f / 8 hours", hoursWorked));
    }

    private void setupMenuItem(View menuItem, String emoji, String title, String subtitle, boolean showDivider) {
        TextView tvIcon = menuItem.findViewById(R.id.tvIcon);
        TextView tvTitle = menuItem.findViewById(R.id.tvTitle);
        TextView tvSubtitle = menuItem.findViewById(R.id.tvSubtitle);
        View divider = menuItem.findViewById(R.id.divider);

        tvIcon.setText(emoji);
        tvTitle.setText(title);
        tvSubtitle.setText(subtitle);
        divider.setVisibility(showDivider ? View.VISIBLE : View.GONE);
    }

    private void startFlyingTaxiAnimation() {
        for (int i = 0; i < 10; i++) {
            headerBackground.postDelayed(this::addFlyingTaxi, i * 750L);
        }
    }

    private void addFlyingTaxi() {
        if (getContext() == null) return;

        TextView taxi = new TextView(getContext());

        String[] taxiEmojis = {"🚕", "🚖"};
        String randomTaxi = taxiEmojis[(int)(Math.random() * taxiEmojis.length)];
        taxi.setText(randomTaxi);

        int size = 80 + (int)(Math.random() * 70);
        taxi.setTextSize(size / 3);
        taxi.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));

        headerBackground.addView(taxi);

        int width = headerBackground.getWidth();
        int height = headerBackground.getHeight();

        if (width == 0 || height == 0) {
            headerBackground.postDelayed(() -> {
                headerBackground.removeView(taxi);
                addFlyingTaxi();
            }, 100);
            return;
        }

        animateTaxi(taxi, width, height);
    }

    private void animateTaxi(TextView taxi, int width, int height) {
        float startX = (float) (Math.random() * width);
        float startY = (float) (Math.random() * height);
        float endX = (float) (Math.random() * width);
        float endY = (float) (Math.random() * height);

        taxi.setX(startX);
        taxi.setY(startY);

        Path path = new Path();
        path.moveTo(startX, startY);
        path.cubicTo(
                (float) (Math.random() * width), (float) (Math.random() * height),
                (float) (Math.random() * width), (float) (Math.random() * height),
                endX, endY
        );

        ObjectAnimator animator = ObjectAnimator.ofFloat(taxi, View.X, View.Y, path);
        animator.setDuration(5000 + (int) (Math.random() * 5000));
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                headerBackground.removeView(taxi);
                addFlyingTaxi();
            }
        });
        animator.start();
    }
}