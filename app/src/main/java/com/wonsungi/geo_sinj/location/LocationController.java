package com.wonsungi.geo_sinj.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.wonsungi.geo_sinj.model.SinjLocation;

import java.util.List;

public class LocationController {
    public interface Listener {
        void onLocationsReceived(List<SinjLocation> locations);
        void onError(Throwable throwable);
    }

    private static final long REFRESH_INTERVAL_MS = 10_000L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final LocationRepository repository = new LocationRepository();

    private final Context context;
    private final Listener listener;

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {

            repository.getLocations(new LocationsCallback() {
                @Override
                public void onSuccess(List<SinjLocation> locations) {
                    listener.onLocationsReceived(locations);
                }

                @Override
                public void onError(Throwable throwable) {
                    listener.onError(throwable);
                }
            });

            handler.postDelayed(this, REFRESH_INTERVAL_MS);
        }
    };

    public LocationController(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void start() {
        context.startForegroundService(new Intent(context, LocationService.class));
        handler.post(refreshRunnable);
    }

    public Location lastLocation() {
        return LocationService.lastLocation;
    }

    public void stop() {
        handler.removeCallbacks(refreshRunnable);
    }
}
