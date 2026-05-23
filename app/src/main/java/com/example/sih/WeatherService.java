package com.example.sih;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("v1/forecast")
    Call<WeatherResponse> getForecast(
            @Query("latitude") double lat,
            @Query("longitude") double lon,
            @Query("daily") String daily,
            @Query("timezone") String timezone
    );
}