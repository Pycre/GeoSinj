package com.wonsungi.geo_sinj.location;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.wonsungi.geo_sinj.model.SinjLocation;
import com.wonsungi.geo_sinj.network.ApiClient;
import com.wonsungi.geo_sinj.network.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationRepository {

    private static final boolean USE_FAKE_DATA = true;

    private final ApiService apiService;

    public LocationRepository() {
        apiService = ApiClient.getApiService();
    }

    public void sendLocation(String id, double lat, double lon) {
        JsonObject body = new JsonObject();

        body.addProperty("id", id);
        body.addProperty("lat", lat);
        body.addProperty("lon", lon);

        apiService.sendLocation(body).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {}

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("Send.Failure", t.getMessage(), t);
            }
        });
    }

    public void getFakelocations(LocationsCallback callback) {
        List<SinjLocation> locations = new ArrayList<>();
        String testUrl = "https://avatars.githubusercontent.com/u/290980035?v=4";

        locations.add(new SinjLocation(UUID.randomUUID().toString(), "Adrien", testUrl, 48.8566, 2.3522, System.currentTimeMillis()));
        locations.add(new SinjLocation(UUID.randomUUID().toString(),"Lucas", testUrl, 48.8600, 2.3400, System.currentTimeMillis()));
        locations.add(new SinjLocation(UUID.randomUUID().toString(), "Pierre", testUrl,48.8700, 2.3200, System.currentTimeMillis()));
        locations.add(new SinjLocation(UUID.randomUUID().toString(), "Marie", testUrl,48.8500, 2.3700, System.currentTimeMillis()));

        callback.onSuccess(locations);
    }

    public void getLocations(LocationsCallback callback) {
        if (USE_FAKE_DATA) { getFakelocations(callback); return; }
        apiService.getLocations().enqueue(new Callback<>() {

            @Override
            public void onResponse(@NonNull Call<List<SinjLocation>> call, @NonNull Response<List<SinjLocation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SinjLocation>> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }

}
