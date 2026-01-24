package com.example.ubercorp.managers;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.ubercorp.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RouteManager {

    private MapView mapView;
    private Context context;
    private List<Marker> markers = new ArrayList<>();

    public RouteManager(MapView mapView, Context context){
        this.mapView = mapView;
        this.context = context;
    }

    private String buildQueryString(List<GeoPoint> stations){
        StringBuilder sb = new StringBuilder("https://router.project-osrm.org/route/v1/driving/");
        for(GeoPoint station : stations){
            sb.append(station.getLongitude());
            sb.append(",");
            sb.append(station.getLatitude());
            if(station != stations.get(stations.size() - 1)) sb.append(";");
        }
        sb.append("?overview=full&geometries=geojson");
        return sb.toString();
    }

    public List<GeoPoint> getRoute(List<GeoPoint> stations) {
        List<GeoPoint> routePoints = new ArrayList<>();
        try {
            String urlString = buildQueryString(stations);
            Log.i("RouteService", "Requesting route: " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.connect();

            int responseCode = conn.getResponseCode();
            Log.i("RouteService", "Route response code: " + responseCode);

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(result.toString());
                JSONArray coordinates = json.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONArray("coordinates");

                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = coordinates.getJSONArray(i);
                    double lon = point.getDouble(0);
                    double lat = point.getDouble(1);
                    routePoints.add(new GeoPoint(lat, lon));
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            Log.e("RouteService", "Route error: " + e.getMessage(), e);
        }
        return routePoints;
    }

    private void markStations(List<GeoPoint> stations) {
        for (Marker marker : markers){
            marker.remove(mapView);
        }
        markers.clear();

        for (GeoPoint station : stations){
            Marker marker = new Marker(mapView);
            marker.setPosition(station);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_location));
            marker.setVisible(true);
            markers.add(marker);
            mapView.getOverlays().add(marker);
        }
    }

    public void drawRoute(List<GeoPoint> routePoints, List<GeoPoint> stations) {
        if (mapView == null || routePoints == null || routePoints.isEmpty()) return;

        Polyline routeLine = new Polyline();
        routeLine.setPoints(routePoints);
        routeLine.setColor(0xFF0077CC);
        routeLine.setWidth(10.0f);

        mapView.getOverlays().clear();
        mapView.getOverlays().add(routeLine);

        markStations(stations);

        double minLat = routePoints.get(0).getLatitude();
        double maxLat = routePoints.get(0).getLatitude();
        double minLon = routePoints.get(0).getLongitude();
        double maxLon = routePoints.get(0).getLongitude();

        for (GeoPoint p : routePoints) {
            if (p.getLatitude() < minLat) minLat = p.getLatitude();
            if (p.getLatitude() > maxLat) maxLat = p.getLatitude();
            if (p.getLongitude() < minLon) minLon = p.getLongitude();
            if (p.getLongitude() > maxLon) maxLon = p.getLongitude();
        }

        BoundingBox bbox = new BoundingBox(maxLat, maxLon, minLat, minLon);
        mapView.zoomToBoundingBox(bbox, true, 100);

        mapView.invalidate();
    }
}
