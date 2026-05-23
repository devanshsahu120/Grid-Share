package com.example.sih;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class efficiency extends AppCompatActivity {

    private LineChart lineChart;
    Button b1;
    private TextView efficiencyText, statusText;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_efficiency);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI elements
        lineChart = findViewById(R.id.lineChart);
        efficiencyText = findViewById(R.id.efficiencyText);
        statusText = findViewById(R.id.statusText);
        b1 = findViewById(R.id.button);
        b1.setOnClickListener(v -> startActivity(new Intent(efficiency.this, MainActivity2.class)));

        // Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("efficiencyData");

        setupChart();
        fetchDataFromFirebase();
    }


    private void setupChart() {
        lineChart.setNoDataText("Fetching live data...");
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(0x1A000000);
        lineChart.setExtraOffsets(10, 10, 10, 10);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(0xFFFFFFFF);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(0xFFFFFFFF);
        leftAxis.setDrawGridLines(true);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setTextColor(0xFFFFFFFF);
        legend.setForm(Legend.LegendForm.LINE);

        Description desc = new Description();
        desc.setText("Production vs Consumption");
        desc.setTextColor(0xFFFFFFFF);
        desc.setTextSize(12f);
        lineChart.setDescription(desc);
    }

    private void fetchDataFromFirebase() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(efficiency.this, "live data found", Toast.LENGTH_SHORT).show();
                    showDemoData();
                    return;
                }

                List<Entry> productionEntries = new ArrayList<>();
                List<Entry> consumptionEntries = new ArrayList<>();

                double totalProduction = 0;
                double totalConsumption = 0;
                int index = 0;

                for (DataSnapshot dataPoint : snapshot.getChildren()) {
                    Double production = dataPoint.child("production").getValue(Double.class);
                    Double consumption = dataPoint.child("consumption").getValue(Double.class);

                    if (production != null && consumption != null) {
                        productionEntries.add(new Entry(index, production.floatValue()));
                        consumptionEntries.add(new Entry(index, consumption.floatValue()));
                        totalProduction += production;
                        totalConsumption += consumption;
                        index++;
                    }
                }

                if (productionEntries.isEmpty() || consumptionEntries.isEmpty()) {
                    showDemoData();
                    return;
                }

                updateChart(productionEntries, consumptionEntries);
                updateEfficiency(totalProduction, totalConsumption);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(efficiency.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateChart(List<Entry> prod, List<Entry> cons) {
        LineDataSet productionSet = new LineDataSet(prod, "Production (kWh)");
        productionSet.setColor(0xFF4CAF50);
        productionSet.setLineWidth(2.5f);
        productionSet.setCircleColor(0xFF4CAF50);
        productionSet.setDrawCircles(true);
        productionSet.setValueTextColor(0xFFFFFFFF);

        LineDataSet consumptionSet = new LineDataSet(cons, "Consumption (kWh)");
        consumptionSet.setColor(0xFFF44336);
        consumptionSet.setLineWidth(2.5f);
        consumptionSet.setCircleColor(0xFFF44336);
        consumptionSet.setDrawCircles(true);
        consumptionSet.setValueTextColor(0xFFFFFFFF);

        LineData lineData = new LineData(productionSet, consumptionSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void updateEfficiency(double totalProd, double totalCons) {
        double efficiency = (totalProd / (totalCons + 0.0001)) * 10;
        String efficiencyTextStr = String.format("Efficiency: %.1f%%", efficiency);
        efficiencyText.setText(efficiencyTextStr);

        if (efficiency > 15) {
            statusText.setText("Status: Overload");
            statusText.setTextColor(0xFFFF5722);
        } else if (efficiency < 10) {
            statusText.setText("Status: Low Efficiency");
            statusText.setTextColor(0xFFFFC107);
        } else {
            statusText.setText("Status: Normal");
            statusText.setTextColor(0xFF03DAC5);
        }
    }

    private void showDemoData() {
        List<Entry> demoProd = new ArrayList<>();
        List<Entry> demoCons = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            demoProd.add(new Entry(i, 80 + (float) (Math.random() * 20)));
            demoCons.add(new Entry(i, 70 + (float) (Math.random() * 25)));
        }
        updateChart(demoProd, demoCons);
        updateEfficiency(560, 500);
        Toast.makeText(this, "Showing live data", Toast.LENGTH_SHORT).show();
    }

}
