package com.example.ubercorp.fragments;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Base64;
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

import com.example.ubercorp.R;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.DriverService;
import com.example.ubercorp.api.UserService;
import com.example.ubercorp.dto.AccountDTO;
import com.example.ubercorp.dto.CreateUserDTO;
import com.example.ubercorp.dto.CreatedUserDTO;
import com.example.ubercorp.dto.GetProfileDTO;
import com.example.ubercorp.dto.UpdateDriverDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;

public class AccountFragment extends Fragment {
    private static final int MEDIA_IMAGES_PERMISSION = 100;
    private static final int SELECT_IMAGE = 200;
    private String ImagePermission;
    private String currentBase64Image = "";
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ImagePermission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            ImagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        setupMenuItems(view);
        initializeViews(view);
        setupListeners();
        configureMenuForRole("user");
        startFlyingTaxiAnimation();

        view.post(this::loadUserProfile);

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

        // Navigate to Change Requests
        menuRequests.setOnClickListener(v -> navigateToChangeRequests());

        menuChangePassword.setOnClickListener(v -> showChangePassword());
        menuManageUsers.setOnClickListener(v -> navigateToManageUsers());
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), SELECT_IMAGE);
    }

    private Bitmap resizeBitmap(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
        }
        return image;
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);

        return "data:image/jpeg;base64," + base64;
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

                bitmap = resizeBitmap(bitmap, 800, 800);

                currentBase64Image = bitmapToBase64(bitmap);

                Bitmap circularBitmap = getCircularBitmap(bitmap);
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
        setupMenuItem(view.findViewById(R.id.menuManageUsers), "👥",
                "Manage Users", "Add or remove users", true);
        setupMenuItem(view.findViewById(R.id.menuChangePassword), "🔑",
                "Change Password", "Update your password", true);
        setupMenuItem(view.findViewById(R.id.menuDeleteAccount), "🗑️",
                "Delete Account", "Permanently delete your account", false);
        setupMenuItem(view.findViewById(R.id.menuFavorites), "❤️",
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
        fabEditProfile.setVisibility(View.GONE);
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
        fabEditProfile.setVisibility(View.VISIBLE);
    }

    private void saveProfileChanges() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(getContext(), "Name and last name are required", Toast.LENGTH_SHORT).show();
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

        SharedPreferences sharedPref = getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);

        if (token == null) {
            android.util.Log.e("AccountFragment", "No auth token found");
            return;
        }

        UserService userApi = ApiClient.getInstance().createService(UserService.class);
        Call<GetProfileDTO> call = userApi.updateProfile("Bearer " + token, updateDTO);

        call.enqueue(new Callback<GetProfileDTO>() {
            @Override
            public void onResponse(Call<GetProfileDTO> call, retrofit2.Response<GetProfileDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetProfileDTO profile = response.body();

                    tvUserName.setText(firstName + " " + lastName);
                    tvUserEmail.setText(email);
                    currentBase64Image = "";

                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    hideEditProfile();
                } else {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    android.util.Log.e("AccountFragment", "Update failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetProfileDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                android.util.Log.e("AccountFragment", "Update failed", t);
            }
        });
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

        CreateUserDTO userDTO = new CreateUserDTO(
                firstName,
                lastName,
                address,
                phone,
                ""
        );

        UpdateDriverDTO changeRequest = new UpdateDriverDTO(userDTO, null);

        DriverService driverService = ApiClient.getInstance().createService(DriverService.class);
        Call<Void> call = driverService.submitDriverChangeRequest(changeRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Change request submitted for admin approval", Toast.LENGTH_LONG).show();
                    hideEditProfile();
                } else {
                    Toast.makeText(getContext(), "Failed to submit change request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                android.util.Log.e("AccountFragment", "Change request failed", t);
            }
        });
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

    private void loadUserProfile() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);

        if (token == null) {
            android.util.Log.e("AccountFragment", "No auth token found");
            return;
        }

        UserService userApi = ApiClient.getInstance().createService(UserService.class);
        Call<GetProfileDTO> call = userApi.getUser("Bearer " + token);

        call.enqueue(new Callback<GetProfileDTO>() {
            @Override
            public void onResponse(Call<GetProfileDTO> call, retrofit2.Response<GetProfileDTO> response) {
                 if (response.isSuccessful() && response.body() != null) {
                    GetProfileDTO profile = response.body();
                    CreatedUserDTO user = profile.getCreatedUserDTO();
                    AccountDTO account = profile.getAccountDTO();

                    if (user != null) {
                        if (etFirstName != null) etFirstName.setText(user.getName());
                        if (etLastName != null) etLastName.setText(user.getLastName());
                        if (tvUserName != null) {
                            tvUserName.setText(user.getName() + " " + user.getLastName());
                        }
                        if (etPhone != null) etPhone.setText(user.getPhone());
                        if (etAddress != null) etAddress.setText(user.getHomeAddress());
                        if (user.getImage() != null) {
                            setProfileImage(user.getImage());
                        }
                    }

                    if (account != null && account.getEmail() != null) {
                        if (etEmail != null) etEmail.setText("🔒 " + account.getEmail());
                        if (tvUserEmail != null) tvUserEmail.setText(account.getEmail());
                    }
                } else {
                    android.util.Log.e("AccountFragment", "Response not successful: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            android.util.Log.e("AccountFragment", "Error: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetProfileDTO> call, Throwable t) {
                android.util.Log.e("AccountFragment", "API FAILED", t);
            }
        });
    }

    private void setProfileImage(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            ivProfilePic.setImageResource(R.drawable.ic_account);
            return;
        }

        try {
            String cleanBase64 = base64Image;
            if (base64Image.contains(",")) {
                cleanBase64 = base64Image.split(",")[1];
            }

            byte[] decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (bitmap != null) {
                Bitmap circularBitmap = getCircularBitmap(bitmap);
                ivProfilePic.setImageBitmap(circularBitmap);
            } else {
                ivProfilePic.setImageResource(R.drawable.ic_account);
            }
        } catch (Exception e) {
            android.util.Log.e("AccountFragment", "Error decoding profile image", e);
            ivProfilePic.setImageResource(R.drawable.ic_account);
        }
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int diameter = Math.min(width, height);

        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        float left = (diameter - width) / 2f;
        float top = (diameter - height) / 2f;
        canvas.drawBitmap(bitmap, left, top, paint);

        return output;
    }
}