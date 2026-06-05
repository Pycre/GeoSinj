package com.wonsungi.geo_sinj;

import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.wonsungi.geo_sinj.databinding.ActivityMainBinding;
import com.wonsungi.geo_sinj.location.LocationRepository;
import com.wonsungi.geo_sinj.location.LocationService;
import com.wonsungi.geo_sinj.location.LocationsCallback;
import com.wonsungi.geo_sinj.model.SinjLocation;
import com.wonsungi.geo_sinj.ui.SinjOverlay;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SinjOverlay.OnSinjClickListener {

    private static final int LOCATION_REQUEST_CODE = 100;

    private ActivityMainBinding binding;
    private LocationRepository repository;
    private SinjOverlay overlay;
    private Handler handler;

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadFakeLocations();
            handler.postDelayed(this, 10000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new LocationRepository();
        overlay = new SinjOverlay(this, this);
        handler = new Handler(Looper.getMainLooper());

        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("osmdroid", MODE_PRIVATE));
        binding.mapView.setMultiTouchControls(true);
        binding.mapView.getOverlays().add(overlay);
        binding.mapView.getController().setZoom(12.0);
        binding.mapView.getController().setCenter(new GeoPoint(48.8566, 2.3522));
        binding.topDrawer.post(() -> binding.topDrawer.setTranslationY(-binding.topDrawer.getHeight()));

        checkLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationService();
            handler.post(refreshRunnable);
        }
    }

    @Override
    public void onSinjClick(SinjLocation sinj) {
        if (sinj == null) { hideTopDrawer(); return; }
        Glide.with(binding.ivAvatar).load(sinj.getAvatarUrl()).circleCrop().into(binding.ivAvatar);
        binding.tvName.setText(sinj.getId());
        binding.tvCoords.setText(String.format("%s, %s", sinj.getLat(), sinj.getLon()));
        binding.tvLastSeen.setText(getRelativeTime(sinj.getTimestamp()));
        showTopDrawer();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(refreshRunnable);
        super.onDestroy();
    }

    private void showTopDrawer() {
        if (binding.topDrawer.getVisibility() == VISIBLE) return;
        binding.topDrawer.setVisibility(VISIBLE);
        binding.topDrawer.animate()
                .translationY(0)
                .setDuration(250)
                .start();
    }

    private void hideTopDrawer() {
        binding.topDrawer.animate()
                .translationY(-binding.topDrawer.getHeight())
                .setDuration(250)
                .withEndAction(() -> binding.topDrawer.setVisibility(View.INVISIBLE))
                .start();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationService();
            handler.post(refreshRunnable);
            return;
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    private void startLocationService() {
        startForegroundService(new Intent(this, LocationService.class));
    }

    private String getRelativeTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;

        long seconds = diff / 1000;
        if (seconds < 60) return "il y a " + seconds + "s";

        long minutes = seconds / 60;
        if (minutes < 60) return "il y a " + minutes + "min";

        long hours = minutes / 60;
        if (hours < 24) return "il y a " + hours + "h";

        long days = hours / 24;
        return "il y a " + days + "j";
    }

    private void updateMap(List<SinjLocation> locations) {
        overlay.submitLocations(locations);
        binding.mapView.invalidate();
    }

    private void loadLocations() {
        repository.getLocations(new LocationsCallback() {
            @Override
            public void onSuccess(List<SinjLocation> locations) {
                runOnUiThread(() -> updateMap(locations));
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", throwable.getMessage(), throwable);
            }
        });
    }

    private void loadFakeLocations() {

        List<SinjLocation> locations = new ArrayList<>();
        String testUrl = "https://cdn-icons-png.flaticon.com/512/3541/3541871.png";
        locations.add(new SinjLocation("Adrien", testUrl, 48.8566, 2.3522, System.currentTimeMillis()));
        locations.add(new SinjLocation("Lucas", testUrl, 48.8600, 2.3400, System.currentTimeMillis()));
        locations.add(new SinjLocation("Pierre", testUrl,48.8700, 2.3200, System.currentTimeMillis()));
        locations.add(new SinjLocation("Marie", testUrl,48.8500, 2.3700, System.currentTimeMillis()));

        updateMap(locations);
    }
}