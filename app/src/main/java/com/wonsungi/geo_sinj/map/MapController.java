package com.wonsungi.geo_sinj.map;

import android.location.Location;

import com.wonsungi.geo_sinj.location.LocationService;
import com.wonsungi.geo_sinj.model.SinjLocation;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.List;

public class MapController {

    private final MapView mapView;
    private final MapOverlay overlay;
    private boolean isFirstCentered = false;

    public MapController(MapView mapView, MapOverlay overlay) {
        this.mapView = mapView;
        this.overlay = overlay;

        mapView.setMultiTouchControls(true);
        mapView.getOverlays().add(overlay);
        mapView.getController().setZoom(12.0);
    }

    public void updateLocations(List<SinjLocation> locations) {
        overlay.submitLocations(locations);

        if (!isFirstCentered && LocationService.lastLocation != null) {
            centerOn(LocationService.lastLocation);
            isFirstCentered = true;
        }

        mapView.invalidate();
    }

    public void centerOn(Location location) {
        if (location == null) return;
        mapView.getController().animateTo(new GeoPoint(location.getLatitude(), location.getLongitude()));
    }

}
