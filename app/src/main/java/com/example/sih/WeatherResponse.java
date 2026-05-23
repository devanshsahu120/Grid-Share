package com.example.sih;

import java.util.List;

public class WeatherResponse {
    public Daily daily;

    public static class Daily {
        public List<String> time;
        public List<Double> temperature_2m_max;
        public List<Double> temperature_2m_min;
        public List<Integer> weathercode;
        public List<Double> shortwave_radiation_sum;

    }
}