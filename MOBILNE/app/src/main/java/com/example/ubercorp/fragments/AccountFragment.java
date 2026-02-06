package com.example.ubercorp.fragments;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.ubercorp.R;
import com.example.ubercorp.dto.AccountDTO;
import com.example.ubercorp.dto.ChangePasswordDTO;
import com.example.ubercorp.dto.CreateUserDTO;
import com.example.ubercorp.dto.DriverDTO;
import com.example.ubercorp.dto.FavoriteRouteDTO;
import com.example.ubercorp.dto.UpdateDriverDTO;
import com.example.ubercorp.dto.VehicleDTO;
import com.example.ubercorp.dto.VehicleTypeDTO;
import com.example.ubercorp.managers.DriverManager;
import com.example.ubercorp.managers.ProfileManager;
import com.example.ubercorp.managers.RideManager;
import com.example.ubercorp.utils.ImageHelper;
import com.example.ubercorp.utils.JwtUtils;
import com.example.ubercorp.utils.MenuConfigurator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment implements
        ProfileManager.ProfileUpdateListener,
        DriverManager.DriverActionsListener {

    // Managers
    private ProfileManager profileManager;
    private DriverManager driverManager;
    private RideManager rideManager;
    private static final int MEDIA_IMAGES_PERMISSION = 100;
    private static final int SELECT_IMAGE = 200;
    private String ImagePermission;
    private String currentBase64Image = "";
    private VehicleDTO currentVehicle;
    private DriverDTO currentDriver;
    private String userRole = "passenger";

    // Header & Profile
    private FrameLayout headerBackground, changePassword;
    private LinearLayout userInfoSection;
    private FloatingActionButton fabEditProfile;
    private TextView tvUserName, tvUserEmail;

    // Cards
    private MaterialCardView editFormCard, menuCard, editVehicleCard;

    // Profile Edit
    private TextInputEditText etFirstName, etLastName, etEmail, etAddress, etPhone;
    private ImageView ivProfilePic;
    private MaterialButton btnCancel, btnSaveChanges, btnSendChanges, btnSavePassword, btnCancelPassword;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private TextView tvReq1, tvReq2, tvReq3;
    private String existingImage;

    // Vehicle Edit
    private TextView tvVehicleModel, tvVehiclePlate;
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
            menuChangePassword, menuFavorites,
            menuUserStat, menuDriverStat, menuVehicle, vehiclePrices;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ImagePermission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            ImagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        // Initialize managers
        profileManager = new ProfileManager(getContext(), this);
        driverManager = new DriverManager(getContext(), this);
        rideManager = new RideManager(getContext());

        loadUserRoleFromToken();
        initializeViews(view);
        setupMenuItems(view);
        setupListeners();

        MenuConfigurator.MenuViews menuViews = createMenuViews();
        MenuConfigurator.configureForRole(userRole, menuViews);

        startFlyingTaxiAnimation();

        view.post(this::loadRoleBasedData);

        return view;
    }

    private void loadRoleBasedData() {
        if ("driver".equals(userRole)) {
            driverManager.loadDriverData();
        } else {
            profileManager.loadProfile();
        }
    }

    private void loadUserRoleFromToken() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);

        if (token != null) {
            if (JwtUtils.isTokenExpired(token)) {
                android.util.Log.w("AccountFragment", "Token has expired");
                return;
            }

            String role = JwtUtils.getRoleFromToken(token);
            if (role != null) {
                userRole = role;
                android.util.Log.d("AccountFragment", "User role from token: " + userRole);
            }

            String email = JwtUtils.getEmailFromToken(token);
            if (email != null) {
                android.util.Log.d("AccountFragment", "User email from token: " + email);
            }
        } else {
            android.util.Log.e("AccountFragment", "No auth token found");
        }
    }

    // ProfileManager callbacks
    @Override
    public void onProfileLoaded(CreateUserDTO user, AccountDTO account) {
        displayUserInfo(user, account);
    }

    @Override
    public void onProfileUpdateSuccess() {
        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
        hideEditProfile();
    }

    @Override
    public void onProfileUpdateFailed(String error) {
        Toast.makeText(getContext(), "Failed: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordChangeSuccess(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        clearPasswordFields();
        hideChangePassword();
    }

    @Override
    public void onPasswordChangeFailed(String error) {
        Toast.makeText(getContext(), "Failed: " + error, Toast.LENGTH_SHORT).show();
    }

    // DriverManager callbacks
    @Override
    public void onDriverDataLoaded(DriverDTO driver) {
        currentDriver = driver;
        displayUserInfo(driver.getCreateUser(), driver.getAccount());
        if (driver.getVehicleDTO() != null) {
            currentVehicle = driver.getVehicleDTO();
            displayVehicleInfo(driver.getVehicleDTO());
        }
        if (driver.getUptime() != null) {
            float hours = driver.getUptime() / 60f;
            float roundedHours = Math.round(hours * 10f) / 10f;
            updateDriverHours(roundedHours);
        }

    }

    @Override
    public void onVehicleUpdateSuccess() {
        Toast.makeText(getContext(), "Change request submitted", Toast.LENGTH_LONG).show();
        hideEditVehicle();
    }

    @Override
    public void onActionFailed(String error) {
        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    private void displayUserInfo(CreateUserDTO user, AccountDTO account) {
        if (user != null) {
            if (etFirstName != null) etFirstName.setText(user.getName());
            if (etLastName != null) etLastName.setText(user.getLastName());
            if (tvUserName != null) {
                tvUserName.setText(user.getName() + " " + user.getLastName());
            }
            if (etPhone != null) etPhone.setText(user.getPhone());
            if (etAddress != null) etAddress.setText(user.getHomeAddress());
            if (user.getImage() != null && !user.getImage().isEmpty()) {
                existingImage = user.getImage();
                ImageHelper.setProfileImage(user.getImage(), ivProfilePic);
            }

        }

        if (account != null && account.getEmail() != null) {
            if (etEmail != null) etEmail.setText("🔒 " + account.getEmail());
            if (tvUserEmail != null) tvUserEmail.setText(account.getEmail());
        }
    }

    private void displayVehicleInfo(VehicleDTO vehicle) {
        if (vehicle == null) return;

        if (vehicle.getModel() != null && tvVehicleModel != null) {
            tvVehicleModel.setText(vehicle.getModel());
        }
        if (vehicle.getPlate() != null && tvVehiclePlate != null) {
            tvVehiclePlate.setText(vehicle.getPlate());
        }

        VehicleTypeDTO vehicleType = vehicle.getVehicleTypeDTO();
        if (vehicleType != null && vehicleType.getName() != null) {
            String typeName = vehicleType.getName();
            if ("STANDARD".equalsIgnoreCase(typeName)) {
                rgVehicleType.check(R.id.rbStandard);
            } else if ("LUXURY".equalsIgnoreCase(typeName)) {
                rgVehicleType.check(R.id.rbLuxury);
            } else if ("VAN".equalsIgnoreCase(typeName)) {
                rgVehicleType.check(R.id.rbVan);
            }
        }

        if (vehicle.getSeatNumber() != null && etNumberOfSeats != null) {
            etNumberOfSeats.setText(String.valueOf(vehicle.getSeatNumber()));
        }

        if (vehicle.getBabySeat() != null && cbBabyTransport != null) {
            cbBabyTransport.setChecked(vehicle.getBabySeat());
        }

        if (vehicle.getPetFriendly() != null && cbPetTransport != null) {
            cbPetTransport.setChecked(vehicle.getPetFriendly());
        }
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
        etEmail.setFocusable(false);
        etEmail.setClickable(false);
        etEmail.setLongClickable(false);
        etPhone = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnSendChanges = view.findViewById(R.id.btnSendChanges);
        btnSavePassword = view.findViewById(R.id.btnSavePassword);
        btnCancelPassword = view.findViewById(R.id.btnCancelPassword);
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        tvReq1 = view.findViewById(R.id.tvReq1);
        tvReq2 = view.findViewById(R.id.tvReq2);
        tvReq3 = view.findViewById(R.id.tvReq3);

        // Vehicle Edit
        tvVehicleModel = view.findViewById(R.id.etVehicleModel);
        tvVehiclePlate = view.findViewById(R.id.etLicensePlate);
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
        menuFavorites = view.findViewById(R.id.menuFavorites);
        menuUserStat = view.findViewById(R.id.menuMyStatistics);
        menuDriverStat = view.findViewById(R.id.menuDriverStatistics);
        menuVehicle = view.findViewById(R.id.menuVehicle);
        vehiclePrices = view.findViewById(R.id.vehiclePrices);
    }

    private void setupListeners() {
        // Profile Edit
        fabEditProfile.setOnClickListener(v -> showEditProfile());
        btnCancel.setOnClickListener(v -> hideEditProfile());
        btnSaveChanges.setOnClickListener(v -> saveProfileChanges());
        btnSendChanges.setOnClickListener(v -> sendProfileChanges());
        btnSavePassword.setOnClickListener(v -> savePassword());
        btnCancelPassword.setOnClickListener(v -> hideChangePassword());

        ivProfilePic.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), ImagePermission)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{ImagePermission}, MEDIA_IMAGES_PERMISSION);
            } else {
                selectImage();
            }
        });

        // Vehicle Edit
        menuVehicle.setOnClickListener(v -> showEditVehicle());
        btnCancelVehicle.setOnClickListener(v -> hideEditVehicle());
        btnSaveVehicle.setOnClickListener(v -> saveVehicleChanges());

        vehiclePrices.setOnClickListener(v -> navigateToVehiclePrices());

        // Navigate to Change Requests
        menuRequests.setOnClickListener(v -> navigateToChangeRequests());
        menuChangePassword.setOnClickListener(v -> showChangePassword());
        menuManageUsers.setOnClickListener(v -> navigateToManageUsers());
        menuFavorites.setOnClickListener(v -> showFavoriteRoutesDialog());
    }

    private MenuConfigurator.MenuViews createMenuViews() {
        MenuConfigurator.MenuViews menuViews = new MenuConfigurator.MenuViews();
        menuViews.menuPlatformStats = menuPlatformStats;
        menuViews.menuRequests = menuRequests;
        menuViews.menuManageUsers = menuManageUsers;
        menuViews.menuChangePassword = menuChangePassword;
        menuViews.menuFavorites = menuFavorites;
        menuViews.menuUserStat = menuUserStat;
        menuViews.menuDriverStat = menuDriverStat;
        menuViews.menuVehicle = menuVehicle;
        menuViews.menuvehiclePrices = vehiclePrices;
        menuViews.drivingHoursSection = drivingHoursSection;
        menuViews.tvUserEmail = tvUserEmail;
        menuViews.changePassword = changePassword;
        menuViews.btnSaveChanges = btnSaveChanges;
        menuViews.btnSendChanges = btnSendChanges;
        return menuViews;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), SELECT_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MEDIA_IMAGES_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_IMAGE && data != null) {
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                bitmap = ImageHelper.resizeBitmap(bitmap, 800, 800);
                currentBase64Image = ImageHelper.bitmapToBase64(bitmap);

                Bitmap circularBitmap = ImageHelper.getCircularBitmap(bitmap);
                ivProfilePic.setImageBitmap(circularBitmap);

                Toast.makeText(getContext(), "Image selected. Save changes to update.", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupMenuItems(View view) {
        setupMenuItem(view.findViewById(R.id.menuPlatformStats), "📊",
                "Platform Statistics", "Get reports", true);
        setupMenuItem(view.findViewById(R.id.menuRequests), "📥",
                "Requests", "Manage change requests", true);
        setupMenuItem(view.findViewById(R.id.menuManageUsers), "🚗",
                "Register driver", "Add new drivers", true);
        setupMenuItem(view.findViewById(R.id.menuChangePassword), "🔑",
                "Change Password", "Update your password", false);
        setupMenuItem(view.findViewById(R.id.menuFavorites), "❤️",
                "Favorites", "Manage favorite routes", true);
        setupMenuItem(view.findViewById(R.id.menuMyStatistics), "📊",
                "My Statistics", "Get reports", true);
        setupMenuItem(view.findViewById(R.id.menuDriverStatistics), "📊",
                "Driver Statistics", "Get reports", true);
        setupMenuItem(view.findViewById(R.id.menuVehicle), "🚗",
                "My Vehicle", "Manage your vehicle", true);
        setupMenuItem(view.findViewById(R.id.vehiclePrices), "💸",
                "Vehicle Prices", "Edit prices", true);
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

    private void navigateToChangeRequests() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_account_to_changeRequests);
    }

    private void navigateToManageUsers() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_account_to_driver_registration);
    }

    private void navigateToVehiclePrices(){
        Navigation.findNavController(requireView())
                .navigate(R.id.vehiclePriceFragment);
    }

    // Edit Profile
    private void showEditProfile() {
        hideViews(userInfoSection, menuCard, tvDrivingHoursProgress, driverProgress);
        showViews(editFormCard);
        fabEditProfile.setVisibility(View.GONE);
    }

    private void showChangePassword() {
        changePassword.setVisibility(View.VISIBLE);
        hideViews(userInfoSection, menuCard, tvDrivingHoursProgress, driverProgress, ivProfilePic);
    }

    private void hideChangePassword() {
        changePassword.setVisibility(View.GONE);
        showViews(userInfoSection, menuCard, tvDrivingHoursProgress, driverProgress, ivProfilePic);
    }

    private void savePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (currentPassword.isEmpty()) {
            Toast.makeText(getContext(), "Current password is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.isEmpty()) {
            Toast.makeText(getContext(), "New password is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 8) {
            Toast.makeText(getContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.matches(".*[A-Z].*")) {
            Toast.makeText(getContext(), "Password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.matches(".*\\d.*")) {
            Toast.makeText(getContext(), "Password must contain at least one number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(currentPassword, newPassword);
        profileManager.changePassword(changePasswordDTO);
    }

    private void clearPasswordFields() {
        if (etCurrentPassword != null) etCurrentPassword.setText("");
        if (etNewPassword != null) etNewPassword.setText("");
        if (etConfirmPassword != null) etConfirmPassword.setText("");
    }

    private void hideEditProfile() {
        hideViews(editFormCard);
        showViews(userInfoSection, menuCard, tvDrivingHoursProgress, driverProgress);
        fabEditProfile.setVisibility(View.VISIBLE);
    }

    private void saveProfileChanges() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(getContext(), "Name and last name are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Address and phone number are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageToSend = currentBase64Image.isEmpty()
                ? existingImage
                : currentBase64Image;

        CreateUserDTO updateDTO = new CreateUserDTO(
                firstName,
                lastName,
                address,
                phone,
                imageToSend
        );

        profileManager.updateProfile(updateDTO);
    }

    private void sendProfileChanges() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(getContext(), "Name and last name are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Address and phone number are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageToSend = currentBase64Image.isEmpty() ? "" : currentBase64Image;

        CreateUserDTO updateDTO = new CreateUserDTO(
                firstName,
                lastName,
                address,
                phone,
                imageToSend
        );

        UpdateDriverDTO changeRequest = new UpdateDriverDTO(updateDTO, currentVehicle, null);
        driverManager.submitChangeRequest(changeRequest);
        hideEditProfile();
    }

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
        String vehicleTypeName = selectedId == R.id.rbStandard ? "STANDARD" :
                selectedId == R.id.rbLuxury ? "LUXURY" : "VAN";

        String seatsStr = etNumberOfSeats.getText().toString().trim();
        if (seatsStr.isEmpty()) {
            Toast.makeText(getContext(), "Number of seats is required", Toast.LENGTH_SHORT).show();
            return;
        }

        int seatNumber;
        try {
            seatNumber = Integer.parseInt(seatsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number of seats", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean babyTransport = cbBabyTransport.isChecked();
        boolean petTransport = cbPetTransport.isChecked();

        Long typeId = vehicleTypeName.equals("STANDARD") ? 1L :
                vehicleTypeName.equals("LUXURY") ? 2L : 3L;
        VehicleTypeDTO vehicleType = new VehicleTypeDTO(typeId, vehicleTypeName, 0.0);

        String model = tvVehicleModel.getText().toString().trim();
        if (model.isEmpty()) {
            Toast.makeText(getContext(), "Vehicle model is required", Toast.LENGTH_SHORT).show();
            return;
        }
        String plate = tvVehiclePlate.getText().toString().trim();
        if (plate.isEmpty()) {
            Toast.makeText(getContext(), "License plate is required", Toast.LENGTH_SHORT).show();
            return;
        }

        VehicleDTO updatedVehicle = new VehicleDTO(
                vehicleType,
                model,
                plate,
                seatNumber,
                babyTransport,
                petTransport
        );

        UpdateDriverDTO changeRequest = new UpdateDriverDTO(currentDriver.getCreateUser(), updatedVehicle, null);
        driverManager.submitChangeRequest(changeRequest);
        currentVehicle = updatedVehicle;
    }

    private static final float MAX_HOURS_PER_DAY = 8f;

    private void updateDriverHours(float hoursWorked) {
        driverProgress.setMax(100);

        float progressPercent = (hoursWorked / MAX_HOURS_PER_DAY) * 100f;
        driverProgress.setProgress((int) progressPercent);

        tvDrivingHoursProgress.setText(
                String.format("%.1f / %.0f hours", hoursWorked, MAX_HOURS_PER_DAY)
        );
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

    private void showViews(View... views) {
        for (View view : views) {
            if (view != null) view.setVisibility(View.VISIBLE);
        }
    }

    private void hideViews(View... views) {
        for (View view : views) {
            if (view != null) view.setVisibility(View.GONE);
        }
    }

    private void showFavoriteRoutesDialog() {
        FavoriteRoutesDialog dialog = FavoriteRoutesDialog.newInstance();

        dialog.setLoading(true);
        dialog.setEditMode(true);
        dialog.show(getParentFragmentManager(), FavoriteRoutesDialog.TAG);

        dialog.setOnRouteRemovedListener(route -> {
            removeRouteFromFavorites(route, dialog);
        });

        loadFavoriteRoutes(dialog);
    }

    private void loadFavoriteRoutes(FavoriteRoutesDialog dialog) {
        rideManager.getFavoriteRoutes(new Callback<List<FavoriteRouteDTO>>() {
            @Override
            public void onResponse(Call<List<FavoriteRouteDTO>> call,
                                   Response<List<FavoriteRouteDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dialog.setLoading(false);
                    dialog.setRoutes(response.body());
                } else {
                    dialog.setLoading(false);
                    Toast.makeText(requireContext(),
                            "Failed to load favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteRouteDTO>> call, Throwable t) {
                dialog.setLoading(false);
                Toast.makeText(requireContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeRouteFromFavorites(FavoriteRouteDTO route, FavoriteRoutesDialog dialog) {
        rideManager.removeFromFavoritesByRouteId(route.getRouteDTO().getId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(),
                            "Route removed from favorites", Toast.LENGTH_SHORT).show();
                    loadFavoriteRoutes(dialog);
                } else {
                    Toast.makeText(requireContext(),
                            "Failed to remove route", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}