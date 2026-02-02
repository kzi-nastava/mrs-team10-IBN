package com.example.ubercorp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ubercorp.R;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.RideService;
import com.example.ubercorp.dto.CancelRideDTO;
import com.example.ubercorp.dto.CoordinateDTO;
import com.example.ubercorp.dto.IncomingRideDTO;
import com.example.ubercorp.managers.RideManager;
import com.example.ubercorp.managers.RouteManager;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class IncomingRideFragment extends Fragment {

    LinearLayout stopsContainer;
    MapView mapView;
    Button startButton;
    Button declineButton;
    RideManager rideManager;
    RouteManager routeManager;
    Long rideID;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incoming_ride, container, false);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        mapView = view.findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(12.75);
        mapView.getController().setCenter(new GeoPoint(45.242, 19.8227));

        stopsContainer = view.findViewById(R.id.stopsContainer);
        startButton = view.findViewById(R.id.startButton);
        declineButton = view.findViewById(R.id.declineButton);

        routeManager = new RouteManager(mapView, this.getContext());

        context = this.getContext();

        declineButton.setOnClickListener((v) -> {
            final EditText input = new EditText(this.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext()).setTitle("Logout")
                    .setMessage("Enter cancellation reason:")
                    .setView(input)
                    .setPositiveButton("Send", (dialog, which) -> {
                        rideManager.cancelRide(rideID, input.getText().toString(), true, new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                            .setTitle("Ride Cancelled")
                                            .setMessage("Ride cancelled successfully")
                                            .setPositiveButton("OK", (dialog, which) -> {
                                                Navigation.findNavController(requireView()).navigate(R.id.action_incomingRideFragment_to_routeFragment);
                                            });
                                    builder.show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                        .setTitle("Cancellation Failed!")
                                        .setMessage("Cancellation failed! Check your Internet connection and try again...")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            dialog.dismiss();
                                        });
                                builder.show();
                            }
                        });
                    })
                    .setNegativeButton("Close", (dialog, which) -> {
                        dialog.dismiss();
                    });
            builder.setTitle("Decline ride");

            builder.show();
        });

        rideManager = new RideManager(this.getContext());
        rideManager.getIncomingRide(new Callback<IncomingRideDTO>() {
            @Override
            public void onResponse(Call<IncomingRideDTO> call, Response<IncomingRideDTO> response) {
                if(response.isSuccessful()){
                    rideID = response.body().getId();
                    addStationText(response.body().getRoute().getStations());
                    List<GeoPoint> geopoints = new ArrayList<>();
                    for (CoordinateDTO point : response.body().getRoute().getStations()){
                        geopoints.add(point.toGeoPoint());
                    }
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<GeoPoint> route = routeManager.getRoute(geopoints);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    routeManager.drawRoute(route, geopoints);
                                }
                            });
                        }
                    });
                    thread.start();
                }else{
                    TextView startText = new TextView(context);
                    startText.setText("No incoming rides");
                    startText.setTypeface(getResources().getFont(R.font.mandali_regular));
                    startText.setTextSize(12);
                    startText.setTextColor(getResources().getColor(R.color.white));
                    stopsContainer.addView(startText);
                }
            }

            @Override
            public void onFailure(Call<IncomingRideDTO> call, Throwable t) {
                TextView startText = new TextView(context);
                startText.setText("Check your Internet connection and try again!");
                startText.setTypeface(getResources().getFont(R.font.mandali_regular));
                startText.setTextSize(12);
                startText.setTextColor(getResources().getColor(R.color.white));
                stopsContainer.addView(startText);
            }
        });

        return view;
    }

    public void addStationText(List<CoordinateDTO> stations){
        TextView startText = new TextView(this.getContext());
        startText.setText("Start Location: " + stations.get(0).getAddress());
        startText.setTypeface(getResources().getFont(R.font.mandali_regular));
        startText.setTextSize(12);
        startText.setTextColor(getResources().getColor(R.color.white));
        stopsContainer.addView(startText);

        for(int i = 1; i < stations.size() - 1; i++){
            TextView stationText = new TextView(this.getContext());
            stationText.setText("Station: " + stations.get(i).getAddress());
            stationText.setTypeface(getResources().getFont(R.font.mandali_regular));
            stationText.setTextSize(12);
            stationText.setTextColor(getResources().getColor(R.color.white));
            stopsContainer.addView(stationText);
        }
        TextView finishText = new TextView(this.getContext());
        finishText.setText("Final Destination: " + stations.get(stations.size() - 1).getAddress());
        finishText.setTypeface(getResources().getFont(R.font.mandali_regular));
        finishText.setTextSize(12);
        finishText.setTextColor(getResources().getColor(R.color.white));
        stopsContainer.addView(finishText);
    }
}