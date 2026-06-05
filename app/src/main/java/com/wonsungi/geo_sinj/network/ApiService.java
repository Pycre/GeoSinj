package com.wonsungi.geo_sinj.network;

import com.google.gson.JsonObject;
import com.wonsungi.geo_sinj.model.SinjLocation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("location")
    Call<Void> sendLocation(@Body JsonObject body);

    @GET("locations")
    Call<List<SinjLocation>> getLocations();

}
