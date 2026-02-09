package com.example.ubercorp.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ubercorp.R;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.StatisticsService;
import com.example.ubercorp.dto.DailyStatDTO;
import com.example.ubercorp.dto.StatisticsDTO;
import com.example.ubercorp.dto.UserBasicDTO;
import com.example.ubercorp.utils.JwtUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsFragment extends Fragment {

    private Button btnStartDate, btnEndDate;
    private RadioGroup viewModeGroup;
    private RadioButton rbAllUsers, rbSingleUser;
    private Spinner spinnerUserType, spinnerUser;
    private LinearLayout adminFiltersLayout, userSelectLayout, statisticsContent;
    private ProgressBar progressBar;
    private TextView tvError;

    private TextView tvTotalRides, tvAvgRides;
    private TextView tvTotalDistance, tvAvgDistance;
    private TextView tvTotalMoney, tvAvgMoney;
    private TextView tvMoneyLabel, tvMoneyChartLabel;

    private BarChart chartRides;
    private LineChart chartDistance, chartMoney;

    private StatisticsService statisticsApi;
    private String userRole;
    private String startDate, endDate;
    private String viewMode = "all";
    private String userType = "drivers";
    private Long selectedUserId = null;
    private List<UserBasicDTO> availableUsers = new ArrayList<>();

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupDates();
        statisticsApi = ApiClient.getInstance().createService(StatisticsService.class);

        loadUserRoleFromToken();

        if ("administrator".equals(userRole)) {
            adminFiltersLayout.setVisibility(View.VISIBLE);
            setupAdminFilters();
        }

        setupListeners();
        loadStatistics();
    }

    private void loadUserRoleFromToken() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", null);

        if (token != null) {
            if (JwtUtils.isTokenExpired(token)) {
                android.util.Log.w("StatisticsFragment", "Token has expired");
                return;
            }

            String role = JwtUtils.getRoleFromToken(token);
            if (role != null) {
                userRole = role;
                android.util.Log.d("StatisticsFragment", "User role from token: " + userRole);
            }
        } else {
            android.util.Log.e("StatisticsFragment", "No auth token found");
        }
    }
    private void initViews(View view) {
        btnStartDate = view.findViewById(R.id.btnStartDate);
        btnEndDate = view.findViewById(R.id.btnEndDate);
        viewModeGroup = view.findViewById(R.id.viewModeGroup);
        rbAllUsers = view.findViewById(R.id.rbAllUsers);
        rbSingleUser = view.findViewById(R.id.rbSingleUser);
        spinnerUserType = view.findViewById(R.id.spinnerUserType);
        spinnerUser = view.findViewById(R.id.spinnerUser);
        adminFiltersLayout = view.findViewById(R.id.adminFiltersLayout);
        userSelectLayout = view.findViewById(R.id.userSelectLayout);
        statisticsContent = view.findViewById(R.id.statisticsContent);
        progressBar = view.findViewById(R.id.progressBar);
        tvError = view.findViewById(R.id.tvError);

        tvTotalRides = view.findViewById(R.id.tvTotalRides);
        tvAvgRides = view.findViewById(R.id.tvAvgRides);
        tvTotalDistance = view.findViewById(R.id.tvTotalDistance);
        tvAvgDistance = view.findViewById(R.id.tvAvgDistance);
        tvTotalMoney = view.findViewById(R.id.tvTotalMoney);
        tvAvgMoney = view.findViewById(R.id.tvAvgMoney);
        tvMoneyLabel = view.findViewById(R.id.tvMoneyLabel);
        tvMoneyChartLabel = view.findViewById(R.id.tvMoneyChartLabel);

        chartRides = view.findViewById(R.id.chartRides);
        chartDistance = view.findViewById(R.id.chartDistance);
        chartMoney = view.findViewById(R.id.chartMoney);
    }

    private void setupDates() {
        Calendar calendar = Calendar.getInstance();
        endDate = dateFormatter.format(calendar.getTime());
        btnEndDate.setText(endDate);

        calendar.add(Calendar.MONTH, -1);
        startDate = dateFormatter.format(calendar.getTime());
        btnStartDate.setText(startDate);
    }

    private void setupAdminFilters() {
        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Drivers", "Passengers"});
        userTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(userTypeAdapter);

        loadAvailableUsers();
    }

    private void setupListeners() {
        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));

        viewModeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAllUsers) {
                viewMode = "all";
                userSelectLayout.setVisibility(View.GONE);
                loadStatistics();
            } else {
                viewMode = "single";
                userSelectLayout.setVisibility(View.VISIBLE);
                statisticsContent.setVisibility(View.GONE);
            }
        });

        spinnerUserType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                userType = position == 0 ? "drivers" : "passengers";
                loadAvailableUsers();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spinnerUser.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedUserId = availableUsers.get(position - 1).getId();
                    loadStatistics();
                } else {
                    selectedUserId = null;
                    statisticsContent.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private String getToken() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        return sharedPref.getString("auth_token", null);
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(isStartDate ? dateFormatter.parse(startDate) : dateFormatter.parse(endDate));
        } catch (Exception e) {}

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                R.style.CustomDatePickerDialog,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);
                    String selectedDate = dateFormatter.format(selectedCalendar.getTime());

                    if (isStartDate) {
                        startDate = selectedDate;
                        btnStartDate.setText(startDate);
                    } else {
                        endDate = selectedDate;
                        btnEndDate.setText(endDate);
                    }

                    loadStatistics();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();

        Button positive = datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE);
        Button negative = datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE);
        int color = ContextCompat.getColor(requireContext(), R.color.btn_color_1);

        positive.setText("OK");
        negative.setText("Cancel");
        positive.setTextColor(color);
        negative.setTextColor(color);

        DatePicker dp = datePickerDialog.getDatePicker();
        dp.setCalendarViewShown(false);
        dp.setSpinnersShown(true);
    }
    private void loadAvailableUsers() {
        Call<List<UserBasicDTO>> call = userType.equals("drivers") ?
                statisticsApi.getAllDrivers("Bearer " + getToken()) : statisticsApi.getAllPassengers("Bearer " + getToken());

        call.enqueue(new Callback<List<UserBasicDTO>>() {
            @Override
            public void onResponse(Call<List<UserBasicDTO>> call, Response<List<UserBasicDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    availableUsers = response.body();

                    List<String> userNames = new ArrayList<>();
                    userNames.add("-- Select a user --");
                    for (UserBasicDTO user : availableUsers) {
                        userNames.add(user.getFullName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            userNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerUser.setAdapter(adapter);

                    selectedUserId = null;
                    if (viewMode.equals("single")) {
                        statisticsContent.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserBasicDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStatistics() {
        if ("administrator".equals(userRole) && "single".equals(viewMode) && selectedUserId == null) {
            return;
        }

        showLoading();

        Call<StatisticsDTO> call;

        if ("administrator".equals(userRole)) {
            if ("all".equals(viewMode)) {
                call = statisticsApi.getAllUsersStatistics("Bearer " + getToken(), userType, startDate, endDate);
            } else {
                call = statisticsApi.getUserStatistics("Bearer " + getToken(), selectedUserId, startDate, endDate);
            }
        } else {
            call = statisticsApi.getMyStatistics("Bearer " + getToken(), startDate, endDate);
        }

        call.enqueue(new Callback<StatisticsDTO>() {
            @Override
            public void onResponse(Call<StatisticsDTO> call, Response<StatisticsDTO> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    displayStatistics(response.body());
                } else {
                    showError("Failed to load statistics");
                }
            }

            @Override
            public void onFailure(Call<StatisticsDTO> call, Throwable t) {
                hideLoading();
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void displayStatistics(StatisticsDTO stats) {
        statisticsContent.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);

        String moneyLabel = getMoneyLabel();
        tvMoneyLabel.setText("TOTAL " + moneyLabel.toUpperCase());
        tvMoneyChartLabel.setText(moneyLabel + " per Day");

        tvTotalRides.setText(String.valueOf(stats.getTotalRides()));
        tvAvgRides.setText(String.format(Locale.getDefault(), "Avg: %.1f per day", stats.getAverageRides()));

        tvTotalDistance.setText(String.format(Locale.getDefault(), "%.1f km", stats.getTotalDistance()));
        tvAvgDistance.setText(String.format(Locale.getDefault(), "Avg: %.1f km per day", stats.getAverageDistance()));

        tvTotalMoney.setText(String.format(Locale.getDefault(), "%.0f RSD", stats.getTotalMoney()));
        tvAvgMoney.setText(String.format(Locale.getDefault(), "Avg: %.0f RSD per day", stats.getAverageMoney()));

        updateBarChart(chartRides, stats.getDailyRides(), "Rides", Color.parseColor("#2196F3"));
        updateLineChart(chartDistance, stats.getDailyDistance(), "Distance (km)", Color.parseColor("#4CAF50"));
        updateLineChart(chartMoney, stats.getDailyMoney(), moneyLabel + " (RSD)", Color.parseColor("#FF9800"));
    }

    private void updateBarChart(BarChart chart, List<DailyStatDTO> data, String label, int color) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            entries.add(new BarEntry(i, (float) data.get(i).getValue()));
            labels.add(formatDateLabel(data.get(i).getDate()));
        }

        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColor(color);
        //dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(45f);

        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setGranularityEnabled(true);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisRight().setEnabled(false);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateY(500);
        chart.invalidate();
    }

    private void updateLineChart(LineChart chart, List<DailyStatDTO> data, String label, int color) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            entries.add(new Entry(i, (float) data.get(i).getValue()));
            labels.add(formatDateLabel(data.get(i).getDate()));
        }

        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(color);
        dataSet.setCircleRadius(4f);
        //dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(color);
        dataSet.setFillAlpha(50);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(45f);

        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setGranularityEnabled(true);
        chart.getAxisLeft().setGranularity(0.1f);
        chart.getAxisRight().setEnabled(false);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateY(500);
        chart.invalidate();
    }

    private String formatDateLabel(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("M/d", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(dateStr));
        } catch (Exception e) {
            return dateStr;
        }
    }

    private String getMoneyLabel() {
        if ("administrator".equals(userRole)) {
            return "drivers".equals(userType) ? "Earned" : "Spent";
        }
        if ("driver".equals(userRole)) {
            return "Earned";
        }
        return "Spent";
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        statisticsContent.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        statisticsContent.setVisibility(View.GONE);
    }
}