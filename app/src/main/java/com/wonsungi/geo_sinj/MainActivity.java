package com.wonsungi.geo_sinj;

import static android.view.View.VISIBLE;

import static com.wonsungi.geo_sinj.utils.DistanceUtils.getDistance;
import static com.wonsungi.geo_sinj.utils.TimeUtils.getRelativeTime;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.wonsungi.geo_sinj.databinding.ActivityMainBinding;
import com.wonsungi.geo_sinj.location.LocationController;
import com.wonsungi.geo_sinj.map.MapController;
import com.wonsungi.geo_sinj.model.SinjLocation;
import com.wonsungi.geo_sinj.map.MapOverlay;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MapOverlay.OnSinjClickListener, LocationController.Listener {

    private static final int LOCATION_REQUEST_CODE = 100;

    private ActivityMainBinding binding;

    private MapController mapController;
    private LocationController locationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        mapController = new MapController(binding.mapView, new MapOverlay(this, this));
        locationController = new LocationController(this, this);

        binding.fabCenter.setOnClickListener(view -> mapController.centerOn(locationController.lastLocation()));
        binding.topDrawer.post(() -> binding.topDrawer.setTranslationY(-binding.topDrawer.getHeight()));

        checkLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationController.start();
        }
    }

    @Override
    public void onLocationsReceived(List<SinjLocation> locations) {
        mapController.updateLocations(locations);
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e("MainActivity", throwable.getMessage(), throwable);
    }

    @Override
    public void onSinjClick(SinjLocation sinj) {
        if (sinj == null) { hideTopDrawer(); return; }

        Glide.with(binding.ivAvatar).load(sinj.avatarUrl()).circleCrop().into(binding.ivAvatar);
        binding.tvName.setText(sinj.name());
        binding.tvDistance.setText(getDistance(this, sinj));
        binding.tvCoords.setText(String.format(Locale.getDefault(), "%.5f, %.5f", sinj.lat(), sinj.lon()));
        binding.tvLastSeen.setText(getRelativeTime(this, sinj.timestamp()));

        showTopDrawer();
    }

    @Override
    protected void onDestroy() {
        locationController.stop();
        super.onDestroy();
    }

    private void showTopDrawer() {
        if (binding.topDrawer.getVisibility() == VISIBLE) return;
        binding.topDrawer.setVisibility(VISIBLE);
        binding.topDrawer.animate().translationY(0).setDuration(250).start();
    }

    private void hideTopDrawer() {
        binding.topDrawer.animate().translationY(-binding.topDrawer.getHeight()).setDuration(250)
                .withEndAction(() -> binding.topDrawer.setVisibility(View.INVISIBLE)).start();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationController.start();
            return;
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }
}