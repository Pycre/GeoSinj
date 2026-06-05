package com.wonsungi.geo_sinj.model;

public class SinjLocation {

    private String id;
    private String avatarUrl;
    private double lat;
    private double lon;
    private long timestamp;

    public SinjLocation(String id, String avatarUrl, double lat, double lon, long timestamp) {
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
