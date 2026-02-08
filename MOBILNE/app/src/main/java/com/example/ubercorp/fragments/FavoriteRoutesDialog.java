package com.example.ubercorp.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ubercorp.R;
import com.example.ubercorp.adapters.FavoriteRoutesAdapter;
import com.example.ubercorp.dto.FavoriteRouteDTO;
import java.util.ArrayList;
import java.util.List;

public class FavoriteRoutesDialog extends DialogFragment {

    public static final String TAG = "FavoriteRoutesDialog";

    private View rootView;
    private RecyclerView rvRoutes;
    private LinearLayout llEmptyState;
    private LinearLayout llLoadingState;
    private ImageButton btnClose;

    private List<FavoriteRouteDTO> favoriteRoutes = new ArrayList<>();
    private boolean isEditMode = false;
    private boolean isLoading = false;

    private FavoriteRoutesAdapter routesAdapter;

    private OnRouteSelectedListener onRouteSelectedListener;
    private OnRouteRemovedListener onRouteRemovedListener;

    public interface OnRouteSelectedListener {
        void onRouteSelected(FavoriteRouteDTO route);
    }

    public interface OnRouteRemovedListener {
        void onRouteRemoved(FavoriteRouteDTO route);
    }

    public static FavoriteRoutesDialog newInstance() {
        return new FavoriteRoutesDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.favorites, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews();
        setupDialog();
        setupRecyclerView();
        setupClickListeners();
        updateUI();
    }

    private void findViews() {
        rvRoutes = rootView.findViewById(R.id.rvRoutes);
        llEmptyState = rootView.findViewById(R.id.llEmptyState);
        llLoadingState = rootView.findViewById(R.id.llLoadingState);
        btnClose = rootView.findViewById(R.id.btnClose);
    }

    private void setupDialog() {
        rootView.setOnClickListener(v -> dismiss());
    }

    private void setupRecyclerView() {
        routesAdapter = new FavoriteRoutesAdapter(
                isEditMode,
                route -> {
                    if (!isEditMode) {
                        if (onRouteSelectedListener != null) {
                            onRouteSelectedListener.onRouteSelected(route);
                        }
                        dismiss();
                    }
                },
                route -> {
                    if (onRouteRemovedListener != null) {
                        onRouteRemovedListener.onRouteRemoved(route);
                    }
                }
        );

        rvRoutes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRoutes.setAdapter(routesAdapter);
        rvRoutes.setItemAnimator(null);
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> dismiss());
    }

    private void updateUI() {
        if (rootView == null || llLoadingState == null) return;

        if (isLoading) {
            showLoading();
        } else if (favoriteRoutes.isEmpty()) {
            showEmptyState();
        } else {
            showRoutes();
        }
    }

    private void showLoading() {
        llLoadingState.setVisibility(View.VISIBLE);
        llEmptyState.setVisibility(View.GONE);
        rvRoutes.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        llLoadingState.setVisibility(View.GONE);
        llEmptyState.setVisibility(View.VISIBLE);
        rvRoutes.setVisibility(View.GONE);
    }

    private void showRoutes() {
        llLoadingState.setVisibility(View.GONE);
        llEmptyState.setVisibility(View.GONE);
        rvRoutes.setVisibility(View.VISIBLE);
        routesAdapter.submitList(new ArrayList<>(favoriteRoutes));
    }

    public void setRoutes(List<FavoriteRouteDTO> routes) {
        this.favoriteRoutes = routes != null ? routes : new ArrayList<>();
        updateUI();
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
        if (isAdded()) {
            updateUI();
        }
    }


    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        if (routesAdapter != null) {
            routesAdapter.setEditMode(editMode);
        }
    }

    public void setOnRouteSelectedListener(OnRouteSelectedListener listener) {
        this.onRouteSelectedListener = listener;
    }

    public void setOnRouteRemovedListener(OnRouteRemovedListener listener) {
        this.onRouteRemovedListener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
        rvRoutes = null;
        llEmptyState = null;
        llLoadingState = null;
        btnClose = null;
    }
}