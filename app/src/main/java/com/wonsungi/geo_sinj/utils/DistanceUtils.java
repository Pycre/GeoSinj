package com.wonsungi.geo_sinj.utils;

import android.content.Context;
import android.location.Location;

import com.wonsungi.geo_sinj.R;
import com.wonsungi.geo_sinj.location.LocationService;
import com.wonsungi.geo_sinj.model.SinjLocation;

public class DistanceUtils {

    public static String getDistance(Context context, SinjLocation sinj) {
        Location myLocation = LocationService.lastLocation;
        if (myLocation == null) return "--";

        Location sinjLocation = new Location("sinj");
        sinjLocation.setLatitude(sinj.lat());
        sinjLocation.setLongitude(sinj.lon());

        float distance = myLocation.distanceTo(sinjLocation);
        if (distance < 1000) return context.getString(R.string.placeholder_distance_m, distance);

        return context.getString(R.string.placeholder_distance_km, distance / 1000f);
    }

}
