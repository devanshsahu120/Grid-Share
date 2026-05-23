package com.example.sih;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    List<String> dates;
    List<Double> maxTemps;
    List<Double> minTemps;
    List<Integer> codes;
    List<Double> solar;

    public WeatherAdapter(List<String> dates, List<Double> maxTemps, List<Double> minTemps,
                          List<Integer> codes, List<Double> solar) {
        this.dates = dates;
        this.maxTemps = maxTemps;
        this.minTemps = minTemps;
        this.codes = codes;
        this.solar = solar;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Format date & day
        String dateStr = dates.get(position);
        holder.dayText.setText(getDayName(dateStr));
        holder.dateText.setText(formatDate(dateStr));

        // Temperatures
        holder.tempText.setText("Max: " + maxTemps.get(position) + "°C | Min: " + minTemps.get(position) + "°C");

        // Condition text
        holder.conditionText.setText(getWeatherDescription(codes.get(position)));

        // Solar Potential
        if (solar != null && position < solar.size()) {
            double value = solar.get(position);
            holder.solarText.setText("Solar Potential: " + String.format("%.1f", value) + " kWh/m²");

            // Optional color-coding for quick visual
            if (value >= 6.0) {
                holder.solarText.setTextColor(Color.parseColor("#2E7D32")); // Dark green (excellent)
            } else if (value >= 4.0) {
                holder.solarText.setTextColor(Color.parseColor("#66BB6A")); // Medium green
            } else {
                holder.solarText.setTextColor(Color.parseColor("#9E9E9E")); // Gray (low sunlight)
            }
        } else {
            holder.solarText.setText("Solar Potential: --");
            holder.solarText.setTextColor(Color.parseColor("#9E9E9E"));
        }

        // Icon
        Picasso.get().load(getWeatherIcon(codes.get(position))).into(holder.weatherIcon);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayText, dateText, tempText, conditionText, solarText;
        ImageView weatherIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            dateText = itemView.findViewById(R.id.dateText);
            tempText = itemView.findViewById(R.id.tempText);
            conditionText = itemView.findViewById(R.id.conditionText);
            solarText = itemView.findViewById(R.id.solarText);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
        }
    }

    private String getWeatherDescription(int code) {
        switch (code) {
            case 0: return "Clear Sky";
            case 1:
            case 2:
            case 3: return "Partly Cloudy";
            case 45:
            case 48: return "Fog";
            case 51:
            case 53:
            case 55: return "Drizzle";
            case 61:
            case 63:
            case 65: return "Rain";
            case 71:
            case 73:
            case 75: return "Snow";
            case 95: return "Thunderstorm";
            default: return "Unknown";
        }
    }

    private String getWeatherIcon(int code) {
        if (code == 0)
            return "https://cdn-icons-png.flaticon.com/512/869/869869.png";
        if (code >= 1 && code <= 3)
            return "https://cdn-icons-png.flaticon.com/512/1163/1163624.png";
        if (code >= 51 && code <= 65)
            return "https://cdn-icons-png.flaticon.com/512/1163/1163657.png";
        if (code >= 71 && code <= 75)
            return "https://cdn-icons-png.flaticon.com/512/642/642102.png";
        if (code == 95)
            return "https://cdn-icons-png.flaticon.com/512/1146/1146869.png";
        return "https://cdn-icons-png.flaticon.com/512/869/869869.png";
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            Date date = input.parse(dateStr);
            return output.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }

    private String getDayName(String dateStr) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = input.parse(dateStr);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(date);
        } catch (ParseException e) {
            return "";
        }
    }
}