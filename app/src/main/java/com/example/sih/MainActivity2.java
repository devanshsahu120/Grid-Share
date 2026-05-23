package com.example.sih;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity {

    RecyclerView weatherRecycler;
    TextView cityName;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Link UI components
        weatherRecycler = findViewById(R.id.weatherRecycler);
        cityName = findViewById(R.id.cityName);
        progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerView
        weatherRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Start loading data
        getWeather();
    }

    private void getWeather() {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);

        service.getForecast(
                22.7196, 75.8577,
                "temperature_2m_max,temperature_2m_min,weathercode,shortwave_radiation_sum",
                "auto"

        ).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    WeatherResponse weather = response.body();

                    if (weather != null && weather.daily != null) {
                        WeatherAdapter adapter = new WeatherAdapter(
                                weather.daily.time,
                                weather.daily.temperature_2m_max,
                                weather.daily.temperature_2m_min,
                                weather.daily.weathercode,
                                weather.daily.shortwave_radiation_sum


                        );
                        weatherRecycler.setAdapter(adapter);
                    } else {
                        Toast.makeText(MainActivity2.this, "No weather data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity2.this, "API response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity2.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}