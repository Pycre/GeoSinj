package com.wonsungi.geo_sinj.location;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.wonsungi.geo_sinj.R;

import java.util.UUID;

public class LocationService extends Service {

    private static final String CHANNEL_ID = "location_channel";
    private FusedLocationProviderClient locationClient;

    private LocationRepository repository;
    private String id;

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult result) {
            Location location = result.getLastLocation();
            if (location == null) return;

            repository.sendLocation(id, location.getLatitude(), location.getLongitude());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        repository = new LocationRepository();

        locationClient = LocationServices.getFusedLocationProviderClient(this);
        id = getOrCreateDeviceId();

        createNotificationChannel();
        startForeground(1, buildNotification());
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000)
                .setMinUpdateIntervalMillis(15000).build();

        try { locationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper()); }
        catch (SecurityException ex) { Log.e("Location.Service", ex.getMessage(), ex); }
    }

    private String getOrCreateDeviceId() {
        SharedPreferences preferences = getSharedPreferences("sinj_prefs", MODE_PRIVATE);
        String id = preferences.getString("sinj_id", null);

        if (id == null) {
            id = UUID.randomUUID().toString();
            preferences.edit().putString("sinj_id", id).apply();
        }

        return id;
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SINJ RADAR")
                .setContentText("GEO SINJ EN COURS")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true).build();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "GeoSinj", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = getSystemService(NotificationManager.class);

        manager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {
        locationClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
