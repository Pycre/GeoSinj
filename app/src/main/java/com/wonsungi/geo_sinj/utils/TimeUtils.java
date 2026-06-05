package com.wonsungi.geo_sinj.utils;

import android.content.Context;

import com.wonsungi.geo_sinj.R;

public class TimeUtils {

    public static String getRelativeTime(Context context, long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        // TODO Implémenter getString avec context
        long seconds = diff / 1000;
        if (seconds < 60) return context.getString(R.string.placeholder_duration_s, seconds);

        long minutes = seconds / 60;
        if (minutes < 60) return context.getString(R.string.placeholder_duration_m, minutes);

        long hours = minutes / 60;
        if (hours < 24) return context.getString(R.string.placeholder_duration_h, hours);

        long days = hours / 24;
        return context.getString(R.string.placeholder_duration_d, days);
    }

}
