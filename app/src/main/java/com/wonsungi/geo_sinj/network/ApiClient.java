package com.wonsungi.geo_sinj.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static final String SERVER_URL = "http://mc.wonsungi.ovh";

    private static ApiService apiService;

    private ApiClient() {}

    public static ApiService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }

        return apiService;
    }

}
