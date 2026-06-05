package com.wonsungi.geo_sinj.location;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.wonsungi.geo_sinj.model.SinjLocation;
import com.wonsungi.geo_sinj.network.ApiClient;
import com.wonsungi.geo_sinj.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationRepository {

    private final ApiService apiService;

    public LocationRepository() {
        apiService = ApiClient.getApiService();
    }

    public void sendLocation(String id, double lat, double lon) {
        JsonObject body = new JsonObject();

        body.addProperty("id", id);
        body.addProperty("lat", lat);
        body.addProperty("lon", lon);

        apiService.sendLocation(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {}

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("Send.Failure", t.getMessage(), t);
            }
        });
    }

    public void getLocations(LocationsCallback callback) {
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
