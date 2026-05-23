package com.example.sih;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class livedatapage extends AppCompatActivity {

    // Declare TextViews for all data fields
    private TextView voltageText, currentText, tempText, lightText, rainText, loadText;
    private DatabaseReference liveDataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_livedatapage);

        // Adjust window insets (for full screen edge-to-edge layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🔹 Initialize TextViews (IDs must match your XML)
        voltageText = findViewById(R.id.textView6);
        currentText = findViewById(R.id.textView22);
        tempText = findViewById(R.id.textView23);
        lightText = findViewById(R.id.textView24);
        rainText = findViewById(R.id.textView25);
        loadText = findViewById(R.id.textView26);

        // 🔹 Reference to your Firebase node
        liveDataRef = FirebaseDatabase.getInstance().getReference("liveData");

        // 🔹 Listen for real-time updates
        liveDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(livedatapage.this, "No data found in Firebase", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Retrieve and safely convert data
                double voltage = getSafeDouble(snapshot.child("voltage").getValue());
                double current = getSafeDouble(snapshot.child("current").getValue());
                double temperature = getSafeDouble(snapshot.child("temperature").getValue());
                double lightIntensity = getSafeDouble(snapshot.child("lightIntensity").getValue());
                String rain = snapshot.child("rain").getValue(String.class);
                String load = snapshot.child("load").getValue(String.class);

                // 🔹 Update UI
                voltageText.setText(voltage + " V");
                currentText.setText(current + " A");
                tempText.setText(temperature + " °C");
                lightText.setText(lightIntensity + " CD");
                rainText.setText(rain != null ? rain : "N/A");
                loadText.setText(load != null ? load : "N/A");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(livedatapage.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Helper function to handle possible null or wrong type values
    private double getSafeDouble(Object value) {
        if (value == null) return 0.0;
        try {
            if (value instanceof Number) return ((Number) value).doubleValue();
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
