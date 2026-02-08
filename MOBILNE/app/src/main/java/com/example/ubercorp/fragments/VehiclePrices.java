package com.example.ubercorp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.ubercorp.R;
import com.example.ubercorp.databinding.FragmentVehiclePricesBinding;
import com.example.ubercorp.dto.VehiclePriceDTO;
import com.example.ubercorp.managers.VehiclePricesManager;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VehiclePrices extends Fragment {
    private FragmentVehiclePricesBinding binding;
    private VehiclePricesManager vehiclePricesManager;
    private List<VehiclePriceDTO> prices;

    public VehiclePrices() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVehiclePricesBinding.inflate(inflater, container, false);
        vehiclePricesManager = new VehiclePricesManager(requireContext());

        loadPrices();
        return binding.getRoot();
    }

    public void loadPrices(){
        vehiclePricesManager.getVehiclePrices(new Callback<>() {
            @Override
            public void onResponse(Call<List<VehiclePriceDTO>> call, Response<List<VehiclePriceDTO>> response) {
                prices = response.body();
                for (VehiclePriceDTO vp: prices)
                    addRow(vp.getVehicleType(), vp.getPrice());
            }
            @Override
            public void onFailure(Call<List<VehiclePriceDTO>> call, Throwable t) {

            }
        });

        Button btn = binding.btnSave;
        btn.setOnClickListener(l -> saveTableData());
    }

    void addRow(String vehicleType, double price) {
        TableLayout tableLayout = binding.tableVehiclePrices;

        TableRow row = new TableRow(requireContext());
        row.setPadding(0, 4, 0, 4);

        TextView tvType = new TextView(requireContext());
        tvType.setText(vehicleType);
        tvType.setGravity(Gravity.CENTER);
        tvType.setTextSize(16f);
        tvType.setPadding(12, 12, 12, 12);
        tvType.setBackgroundResource(R.drawable.table_cell_bg);

        EditText tvPrice = new EditText(requireContext());
        tvPrice.setText(String.valueOf(price));
        tvPrice.setGravity(Gravity.CENTER);
        tvPrice.setTextSize(16f);
        tvPrice.setPadding(12, 12, 12, 12);
        tvPrice.setBackgroundResource(R.drawable.table_cell_bg);

        row.addView(tvType);
        row.addView(tvPrice);

        tableLayout.addView(row);

    }

    private void saveTableData() {
        TableLayout tableLayout = binding.tableVehiclePrices;

        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);

            String type = ((TextView) row.getChildAt(0)).getText().toString();
            EditText etPrice = (EditText) row.getChildAt(1);

            for (VehiclePriceDTO price: prices) {
                double newPrice = 0.0;
                try {
                    newPrice = Double.parseDouble(etPrice.getText().toString().trim());
                } catch (NumberFormatException e) {}
                    if (Objects.equals(price.getVehicleType(), type))
                        price.setPrice(newPrice);
                }

            vehiclePricesManager.saveVehiclePrices(prices);
        }

    }



}