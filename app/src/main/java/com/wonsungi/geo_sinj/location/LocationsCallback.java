package com.wonsungi.geo_sinj.location;

import com.wonsungi.geo_sinj.model.SinjLocation;

import java.util.List;

public interface LocationsCallback {
    void onSuccess(List<SinjLocation> locations);
    void onError(Throwable throwable);
}
