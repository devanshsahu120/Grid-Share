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

public class consumption extends AppCompatActivity {

    TextView textView11, textView12, textView13, textView14;
    DatabaseReference consumptionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consumption);

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize TextViews
        textView11 = findViewById(R.id.textView11); // sum
        textView12 = findViewById(R.id.textView12); // c1
        textView13 = findViewById(R.id.textView13); // c2
        textView14 = findViewById(R.id.textView14); // c3

        // Firebase Reference
        consumptionRef = FirebaseDatabase.getInstance().getReference("ConsumptionData");

        // Fetch data once
        consumptionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(consumption.this, "No data found in Firebase", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Read values safely (handle both number or string)
                double c1 = getSafeDouble(snapshot.child("cons1").getValue());
                double c2 = getSafeDouble(snapshot.child("cons2").getValue());
                double c3 = getSafeDouble(snapshot.child("cons3").getValue());

                // Display each consumption
                textView12.setText(c1 + " kWh");
                textView13.setText(c2 + " kWh");
                textView14.setText(c3 + " kWh");

                // Calculate and display total
                double total = Math.round((c1 + c2 + c3) * 100.0) / 100.0;
                textView11.setText(total + " kWh");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(consumption.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Helper: safely convert number or string to double
    private double getSafeDouble(Object value) {
        if (value == null) return 0.0;
        try {
            if (value instanceof Number) return ((Number) value).doubleValue();
            else return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
