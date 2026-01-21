package com.example.ubercorp.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;

public class MenuConfigurator {

    public static void configureForRole(String role, MenuViews menuViews) {
        hideAllMenuItems(menuViews);

        switch (role) {
            case "admin":
                showAdminMenu(menuViews);
                break;
            case "driver":
                showDriverMenu(menuViews);
                break;
            case "passenger":
                showPassengerMenu(menuViews);
                break;
        }
    }

    private static void hideAllMenuItems(MenuViews views) {
        hideViews(views.menuPlatformStats, views.menuRequests, views.menuManageUsers,
                views.menuChangePassword, views.menuDeleteAccount, views.menuFavorites,
                views.menuUserStat, views.menuVehicle, views.menuDriverStat,
                views.drivingHoursSection);
        views.btnSendChanges.setVisibility(View.GONE);
        views.changePassword.setVisibility(View.GONE);
    }

    private static void showAdminMenu(MenuViews views) {
        showViews(views.menuPlatformStats, views.menuRequests, views.menuManageUsers,
                views.menuChangePassword, views.menuDeleteAccount);
    }

    private static void showDriverMenu(MenuViews views) {
        showViews(views.menuVehicle, views.menuDriverStat, views.menuChangePassword,
                views.menuDeleteAccount);
        views.tvUserEmail.setVisibility(View.GONE);
        views.drivingHoursSection.setVisibility(View.VISIBLE);
        views.btnSaveChanges.setVisibility(View.GONE);
        views.btnSendChanges.setVisibility(View.VISIBLE);
    }

    private static void showPassengerMenu(MenuViews views) {
        showViews(views.menuFavorites, views.menuUserStat, views.menuChangePassword,
                views.menuDeleteAccount);
    }

    private static void showViews(View... views) {
        for (View view : views) {
            if (view != null) view.setVisibility(View.VISIBLE);
        }
    }

    private static void hideViews(View... views) {
        for (View view : views) {
            if (view != null) view.setVisibility(View.GONE);
        }
    }

    public static class MenuViews {
        public LinearLayout menuPlatformStats;
        public LinearLayout menuRequests;
        public LinearLayout menuManageUsers;
        public LinearLayout menuChangePassword;
        public LinearLayout menuDeleteAccount;
        public LinearLayout menuFavorites;
        public LinearLayout menuUserStat;
        public LinearLayout menuDriverStat;
        public LinearLayout menuVehicle;
        public LinearLayout drivingHoursSection;
        public TextView tvUserEmail;
        public View changePassword;
        public MaterialButton btnSaveChanges;
        public MaterialButton btnSendChanges;
    }
}